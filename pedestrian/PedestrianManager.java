package aim4.pedestrian;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * ตัวจัดการคนเดินถนนและทางม้าลาย
 */
public class PedestrianManager {
    /** รายการคนเดินถนนทั้งหมด */
    private List<Pedestrian> pedestrians;
    
    /** รายการทางม้าลายทั้งหมด */
    private List<Crosswalk> crosswalks;
    
    /** ตัวสุ่ม */
    private Random random;
    
    /** ความถี่ในการสร้างคนเดินถนน (คนต่อวินาที) */
    private double pedestrianSpawnRate;
    
    /** เวลาถัดไปที่จะสร้างคนเดินถนน */
    private double nextSpawnTime;
    
    /** ความเร็วการเดินเฉลี่ย (เมตรต่อวินาที) */
    private static final double AVERAGE_WALKING_SPEED = 1.4; // ~5 km/h
    
    /**
     * สร้าง PedestrianManager
     * 
     * @param pedestrianSpawnRate ความถี่ในการสร้างคนเดินถนน
     */
    public PedestrianManager(double pedestrianSpawnRate) {
        this.pedestrians = new ArrayList<>();
        this.crosswalks = new ArrayList<>();
        this.random = new Random();
        this.pedestrianSpawnRate = pedestrianSpawnRate;
        this.nextSpawnTime = 0.0;
    }
    
    /**
     * เพิ่มทางม้าลาย
     */
    public void addCrosswalk(Crosswalk crosswalk) {
        crosswalks.add(crosswalk);
    }
    
    /**
     * อัพเดทระบบคนเดินถนน
     */
    public void update(double currentTime, double timeStep) {
        // สร้างคนเดินถนนใหม่ตามความถี่
        if (currentTime >= nextSpawnTime && !crosswalks.isEmpty()) {
            spawnPedestrian();
            nextSpawnTime = currentTime + (1.0 / pedestrianSpawnRate);
        }
        
        // อัพเดทตำแหน่งคนเดินถนนทั้งหมด
        List<Pedestrian> toRemove = new ArrayList<>();
        for (Pedestrian pedestrian : pedestrians) {
            pedestrian.move(timeStep);
            
            // ลบคนที่ข้ามเสร็จแล้ว
            if (!pedestrian.isCrossing()) {
                Crosswalk crosswalk = pedestrian.getCurrentCrosswalk();
                if (crosswalk != null) {
                    crosswalk.removePedestrian(pedestrian);
                }
                toRemove.add(pedestrian);
            }
        }
        pedestrians.removeAll(toRemove);
    }
    
    /**
     * สร้างคนเดินถนนใหม่ที่ทางม้าลายแบบสุ่ม
     */
    private void spawnPedestrian() {
        if (crosswalks.isEmpty()) return;
        
        // เลือกทางม้าลายแบบสุ่ม
        Crosswalk crosswalk = crosswalks.get(random.nextInt(crosswalks.size()));
        
        // ตรวจสอบว่ามีคนข้ามอยู่แล้วหรือไม่ (ป้องกันการซ้อนทับ)
        if (crosswalk.hasPedestrianCrossing() && 
            crosswalk.getNumberOfCrossingPedestrians() >= 3) {
            return; // มีคนข้ามเกิน 3 คนแล้ว รอก่อน
        }
        
        // สร้างคนเดินถนนที่ด้านใดด้านหนึ่งของทางม้าลาย
        double x = crosswalk.getBounds().x;
        double y = crosswalk.getBounds().y + crosswalk.getBounds().height / 2;
        
        // สุ่มฝั่งที่จะเริ่มข้าม
        boolean startFromRight = random.nextBoolean();
        if (startFromRight) {
            x += crosswalk.getBounds().width;
        }
        
        // เพิ่มระยะห่างระหว่างคนที่ข้าม (ป้องกันการซ้อนทับ)
        double offsetY = (random.nextDouble() - 0.5) * 8.0; // เพิ่มช่องว่างระหว่างคน
        y += offsetY;
        
        // ตรวจสอบว่าตำแหน่งนี้ปลอดภัย (ไม่ซ้อนกับคนอื่น)
        Point2D.Double newPos = new Point2D.Double(x, y);
        for (Pedestrian existingPed : pedestrians) {
            if (newPos.distance(existingPed.getPosition()) < 3.0) {
                return; // ใกล้คนอื่นเกินไป ยกเลิกการสร้าง
            }
        }
        
        // สร้างคนเดินถนนด้วยความเร็วที่แตกต่างกันเล็กน้อย
        double speed = AVERAGE_WALKING_SPEED + (random.nextDouble() - 0.5) * 0.4;
        Pedestrian pedestrian = new Pedestrian(x, y, speed);
        
        // เริ่มข้ามถนน
        pedestrian.startCrossing(crosswalk);
        crosswalk.addPedestrian(pedestrian);
        pedestrians.add(pedestrian);
    }
    
    /**
     * ได้รับรายการคนเดินถนนทั้งหมด
     */
    public List<Pedestrian> getPedestrians() {
        return new ArrayList<>(pedestrians);
    }
    
    /**
     * ได้รับรายการทางม้าลายทั้งหมด
     */
    public List<Crosswalk> getCrosswalks() {
        return new ArrayList<>(crosswalks);
    }
    
    /**
     * ตรวจสอบว่ารถควรหยุดหรือไม่
     */
    public boolean shouldVehicleStop(Point2D vehiclePosition) {
        for (Crosswalk crosswalk : crosswalks) {
            if (crosswalk.hasPedestrianCrossing() && 
                crosswalk.isVehicleNearby(vehiclePosition, crosswalk.getStopDistance() + 10.0)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * ได้รับทางม้าลายที่ใกล้ที่สุดกับตำแหน่งที่กำหนด
     */
    public Crosswalk getNearestCrosswalk(Point2D position) {
        Crosswalk nearest = null;
        double minDistance = Double.MAX_VALUE;
        
        for (Crosswalk crosswalk : crosswalks) {
            double centerX = crosswalk.getBounds().getCenterX();
            double centerY = crosswalk.getBounds().getCenterY();
            double distance = position.distance(centerX, centerY);
            
            if (distance < minDistance) {
                minDistance = distance;
                nearest = crosswalk;
            }
        }
        
        return nearest;
    }
    
    /**
     * ได้รับทางม้าลายที่ใกล้ที่สุดภายในระยะทางที่กำหนด
     * 
     * @param position ตำแหน่งที่ต้องการตรวจสอบ
     * @param maxDistance ระยะทางสูงสุด
     * @return ทางม้าลายที่ใกล้ที่สุด หรือ null ถ้าไม่มีภายในระยะที่กำหนด
     */
    public Crosswalk getNearestCrosswalk(Point2D position, double maxDistance) {
        Crosswalk nearest = null;
        double minDistance = Double.MAX_VALUE;
        
        for (Crosswalk crosswalk : crosswalks) {
            double centerX = crosswalk.getBounds().getCenterX();
            double centerY = crosswalk.getBounds().getCenterY();
            double distance = position.distance(centerX, centerY);
            
            if (distance < minDistance && distance <= maxDistance) {
                minDistance = distance;
                nearest = crosswalk;
            }
        }
        
        return nearest;
    }
}
