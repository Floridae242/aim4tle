package aim4.pedestrian;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * ทางม้าลาย (Crosswalk) - จุดที่คนเดินถนนข้ามถนน
 */
public class Crosswalk {
    /** ขอบเขตของทางม้าลาย */
    private Rectangle2D.Double bounds;
    
    /** รายการคนเดินถนนที่กำลังข้ามอยู่ */
    private List<Pedestrian> crossingPedestrians;
    
    /** จุดเริ่มต้นทางม้าลาย (ฝั่งหนึ่ง) */
    private Point2D.Double startPoint;
    
    /** จุดปลายทางม้าลาย (อีกฝั่งหนึ่ง) */
    private Point2D.Double endPoint;
    
    /** ระยะทางที่รถต้องหยุดก่อนถึงทางม้าลาย (เมตร) */
    private static final double STOP_DISTANCE = 20.0; // เพิ่มจาก 15 เป็น 20 เมตร
    
    /**
     * สร้างทางม้าลาย
     * 
     * @param x ตำแหน่ง x
     * @param y ตำแหน่ง y
     * @param width ความกว้าง
     * @param height ความสูง
     */
    public Crosswalk(double x, double y, double width, double height) {
        this.bounds = new Rectangle2D.Double(x, y, width, height);
        this.crossingPedestrians = new ArrayList<>();
        
        // กำหนดจุดเริ่มต้นและปลายทาง (แนวนอน)
        this.startPoint = new Point2D.Double(x, y + height/2);
        this.endPoint = new Point2D.Double(x + width, y + height/2);
    }
    
    /**
     * เพิ่มคนเดินถนนที่กำลังข้าม
     */
    public void addPedestrian(Pedestrian pedestrian) {
        if (!crossingPedestrians.contains(pedestrian)) {
            crossingPedestrians.add(pedestrian);
        }
    }
    
    /**
     * ลบคนเดินถนนที่ข้ามเสร็จแล้ว
     */
    public void removePedestrian(Pedestrian pedestrian) {
        crossingPedestrians.remove(pedestrian);
    }
    
    /**
     * ตรวจสอบว่ามีคนกำลังข้ามถนนอยู่หรือไม่
     */
    public boolean hasPedestrianCrossing() {
        // ตรวจสอบว่ามีคนที่ยังข้ามไม่เสร็จ
        for (Pedestrian p : new ArrayList<>(crossingPedestrians)) {
            if (p.isCrossing()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * นับจำนวนคนที่กำลังข้ามอยู่
     */
    public int getNumberOfCrossingPedestrians() {
        int count = 0;
        for (Pedestrian p : new ArrayList<>(crossingPedestrians)) {
            if (p.isCrossing()) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * ได้รับรายการคนเดินถนนที่กำลังข้าม
     */
    public List<Pedestrian> getCrossingPedestrians() {
        return new ArrayList<>(crossingPedestrians);
    }
    
    /**
     * ได้รับพื้นที่ของทางม้าลาย
     */
    public Rectangle2D.Double getBounds() {
        return bounds;
    }
    
    /**
     * ตรวจสอบว่าจุดอยู่ในทางม้าลายหรือไม่
     */
    public boolean contains(Point2D point) {
        return bounds.contains(point);
    }
    
    /**
     * ได้รับจุดปลายทางสำหรับคนเดินถนน
     */
    public Point2D.Double getDestinationPoint(Point2D.Double currentPosition) {
        // หาว่าคนอยู่ฝั่งไหนของทางม้าลาย
        double distToStart = currentPosition.distance(startPoint);
        double distToEnd = currentPosition.distance(endPoint);
        
        // ข้ามไปอีกฝั่ง
        return (distToStart < distToEnd) ? endPoint : startPoint;
    }
    
    /**
     * ได้รับระยะห่างที่รถต้องหยุดก่อนทางม้าลาย
     */
    public double getStopDistance() {
        return STOP_DISTANCE;
    }
    
    /**
     * ตรวจสอบว่ารถใกล้ทางม้าลายหรือไม่
     */
    public boolean isVehicleNearby(Point2D vehiclePosition, double threshold) {
        double centerX = bounds.getCenterX();
        double centerY = bounds.getCenterY();
        double distance = vehiclePosition.distance(centerX, centerY);
        return distance <= threshold;
    }
    
    /**
     * ตรวจสอบว่ารถควรหยุดหรือไม่ - ต้องรอให้คนข้ามเสร็จก่อน
     * @param vehiclePosition ตำแหน่งของรถ
     * @return true ถ้ารถควรหยุด
     */
    public boolean shouldVehicleStop(Point2D vehiclePosition) {
        // ถ้ามีคนกำลังข้าม และรถอยู่ใกล้พอ ให้หยุด
        if (hasPedestrianCrossing()) {
            double distanceToCrosswalk = vehiclePosition.distance(
                bounds.getCenterX(),
                bounds.getCenterY()
            );
            
            // รถต้องหยุดถ้าอยู่ในระยะ STOP_DISTANCE หรือใกล้กว่า
            return distanceToCrosswalk <= STOP_DISTANCE + 5.0;
        }
        return false;
    }
}
