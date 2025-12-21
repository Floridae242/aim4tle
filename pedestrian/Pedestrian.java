package aim4.pedestrian;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * คนเดินถนน (Pedestrian)
 */
public class Pedestrian {
    /** ตำแหน่งปัจจุบัน */
    private Point2D.Double position;
    
    /** ความเร็วในการเดิน (เมตรต่อวินาที) */
    private double walkingSpeed;
    
    /** จุดหมายปลายทาง */
    private Point2D.Double destination;
    
    /** สถานะการข้ามถนน */
    private boolean isCrossing;
    
    /** ทางม้าลายที่กำลังข้าม */
    private Crosswalk currentCrosswalk;
    
    /** ขนาดของคนเดินถนน - เพิ่มขนาดให้ใหญ่ขึ้นมาก */
    private static final double WIDTH = 2.5;
    private static final double HEIGHT = 2.5;
    
    /**
     * สร้างคนเดินถนนใหม่
     * 
     * @param x ตำแหน่ง x เริ่มต้น
     * @param y ตำแหน่ง y เริ่มต้น
     * @param walkingSpeed ความเร็วในการเดิน
     */
    public Pedestrian(double x, double y, double walkingSpeed) {
        this.position = new Point2D.Double(x, y);
        this.walkingSpeed = walkingSpeed;
        this.isCrossing = false;
    }
    
    /**
     * เริ่มข้ามถนนที่ทางม้าลาย
     */
    public void startCrossing(Crosswalk crosswalk) {
        this.currentCrosswalk = crosswalk;
        this.destination = crosswalk.getDestinationPoint(this.position);
        this.isCrossing = true;
    }
    
    /**
     * อัพเดทตำแหน่งของคนเดินถนน
     */
    public void move(double timeStep) {
        if (!isCrossing || destination == null) {
            return;
        }
        
        // คำนวณทิศทางการเดิน
        double dx = destination.x - position.x;
        double dy = destination.y - position.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance < walkingSpeed * timeStep) {
            // ถึงจุดหมายแล้ว
            position.setLocation(destination);
            isCrossing = false;
            currentCrosswalk = null;
        } else {
            // เดินไปทีละก้าว
            double moveX = (dx / distance) * walkingSpeed * timeStep;
            double moveY = (dy / distance) * walkingSpeed * timeStep;
            position.x += moveX;
            position.y += moveY;
        }
    }
    
    /**
     * ตรวจสอบว่ากำลังข้ามถนนอยู่หรือไม่
     */
    public boolean isCrossing() {
        return isCrossing;
    }
    
    /**
     * ได้รับตำแหน่งปัจจุบัน
     */
    public Point2D.Double getPosition() {
        return position;
    }
    
    /**
     * ได้รับ bounding box ของคนเดินถนน
     */
    public Rectangle2D.Double getBounds() {
        return new Rectangle2D.Double(
            position.x - WIDTH/2, 
            position.y - HEIGHT/2, 
            WIDTH, 
            HEIGHT
        );
    }
    
    /**
     * ได้รับทางม้าลายที่กำลังข้าม
     */
    public Crosswalk getCurrentCrosswalk() {
        return currentCrosswalk;
    }
    
    /**
     * ได้รับความเร็วในการเดิน
     */
    public double getWalkingSpeed() {
        return walkingSpeed;
    }
}
