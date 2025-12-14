package aim4.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import aim4.pedestrian.Crosswalk;
import aim4.pedestrian.Pedestrian;
import aim4.pedestrian.PedestrianManager;

/**
 * คลาสสำหรับวาดคนเดินถนนและทางม้าลาย
 */
public class PedestrianRenderer {
    
    /** สีของทางม้าลาย */
    private static final Color CROSSWALK_COLOR = Color.WHITE;
    
    /** สีของคนเดินถนน */
    private static final Color PEDESTRIAN_COLOR = new Color(255, 150, 50); // สีส้ม
    
    /** สีเส้นขอบคนเดินถนน */
    private static final Color PEDESTRIAN_BORDER_COLOR = Color.BLACK;
    
    /** ความหนาของเส้นทางม้าลาย */
    private static final float CROSSWALK_STRIPE_WIDTH = 0.5f;
    
    /**
     * วาดคนเดินถนนและทางม้าลายทั้งหมด
     */
    public static void render(Graphics2D g2d, PedestrianManager pedestrianManager, 
                             double scaleFactor) {
        if (pedestrianManager == null) {
            return;
        }
        
        // วาดทางม้าลายก่อน
        renderCrosswalks(g2d, pedestrianManager.getCrosswalks(), scaleFactor);
        
        // วาดคนเดินถนน
        renderPedestrians(g2d, pedestrianManager.getPedestrians(), scaleFactor);
    }
    
    /**
     * วาดทางม้าลายทั้งหมด
     */
    private static void renderCrosswalks(Graphics2D g2d, List<Crosswalk> crosswalks, 
                                        double scaleFactor) {
        for (Crosswalk crosswalk : crosswalks) {
            renderCrosswalk(g2d, crosswalk, scaleFactor);
        }
    }
    
    /**
     * วาดทางม้าลายหนึ่งอัน
     */
    private static void renderCrosswalk(Graphics2D g2d, Crosswalk crosswalk, 
                                       double scaleFactor) {
        Rectangle2D bounds = crosswalk.getBounds();
        
        // วาดพื้นหลังทางม้าลาย
        g2d.setColor(new Color(200, 200, 200, 150)); // สีเทาโปร่งใส
        g2d.fill(bounds);
        
        // วาดเส้นทางม้าลาย (แบบลายทาง)
        g2d.setColor(CROSSWALK_COLOR);
        g2d.setStroke(new BasicStroke((float)(CROSSWALK_STRIPE_WIDTH * scaleFactor)));
        
        double x = bounds.getX();
        double y = bounds.getY();
        double width = bounds.getWidth();
        double height = bounds.getHeight();
        
        // ถ้าทางม้าลายแนวนอน
        if (width > height) {
            double stripeWidth = width / 10.0;
            for (int i = 0; i < 10; i += 2) {
                double stripeX = x + i * stripeWidth;
                g2d.fill(new Rectangle2D.Double(stripeX, y, stripeWidth * 0.8, height));
            }
        } else {
            // ทางม้าลายแนวตั้ง
            double stripeHeight = height / 10.0;
            for (int i = 0; i < 10; i += 2) {
                double stripeY = y + i * stripeHeight;
                g2d.fill(new Rectangle2D.Double(x, stripeY, width, stripeHeight * 0.8));
            }
        }
    }
    
    /**
     * วาดคนเดินถนนทั้งหมด
     */
    private static void renderPedestrians(Graphics2D g2d, List<Pedestrian> pedestrians, 
                                         double scaleFactor) {
        for (Pedestrian pedestrian : pedestrians) {
            renderPedestrian(g2d, pedestrian, scaleFactor);
        }
    }
    
    /**
     * วาดคนเดินถนนหนึ่งคน
     */
    private static void renderPedestrian(Graphics2D g2d, Pedestrian pedestrian, 
                                        double scaleFactor) {
        Rectangle2D bounds = pedestrian.getBounds();
        
        // วาดร่างกาย (วงกลมใหญ่)
        double centerX = bounds.getCenterX();
        double centerY = bounds.getCenterY();
        double radius = Math.max(bounds.getWidth(), bounds.getHeight()) * 1.2; // เพิ่มขนาด 20%
        
        Ellipse2D body = new Ellipse2D.Double(
            centerX - radius/2, 
            centerY - radius/2, 
            radius, 
            radius
        );
        
        // วาดเงาให้ชัดเจน
        g2d.setColor(new Color(0, 0, 0, 80));
        Ellipse2D shadow = new Ellipse2D.Double(
            centerX - radius/2 + 0.3 * scaleFactor, 
            centerY - radius/2 + 0.3 * scaleFactor, 
            radius, 
            radius
        );
        g2d.fill(shadow);
        
        // วาดตัวคนเดินถนน (สีส้มสดใส)
        g2d.setColor(new Color(255, 140, 0)); // สีส้มสดกว่าเดิม
        g2d.fill(body);
        
        // วาดเส้นขอบหนาให้เห็นชัด
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke((float)(0.3 * scaleFactor)));
        g2d.draw(body);
        
        // วาดหัวใหญ่ขึ้น
        double headRadius = radius * 0.45; // เพิ่มขนาดหัว
        Ellipse2D head = new Ellipse2D.Double(
            centerX - headRadius/2, 
            centerY - radius/2 - headRadius * 0.6, 
            headRadius, 
            headRadius
        );
        g2d.setColor(new Color(255, 220, 177)); // สีผิวสดใส
        g2d.fill(head);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke((float)(0.25 * scaleFactor)));
        g2d.draw(head);
        
        // วาดดวงตาให้ชัดเจน
        double eyeSize = headRadius * 0.15;
        double eyeOffsetX = headRadius * 0.25;
        double eyeOffsetY = headRadius * 0.15;
        
        // ตาซ้าย
        Ellipse2D leftEye = new Ellipse2D.Double(
            centerX - eyeOffsetX - eyeSize/2,
            centerY - radius/2 - headRadius * 0.6 - eyeOffsetY,
            eyeSize,
            eyeSize
        );
        g2d.setColor(Color.BLACK);
        g2d.fill(leftEye);
        
        // ตาขวา
        Ellipse2D rightEye = new Ellipse2D.Double(
            centerX + eyeOffsetX - eyeSize/2,
            centerY - radius/2 - headRadius * 0.6 - eyeOffsetY,
            eyeSize,
            eyeSize
        );
        g2d.fill(rightEye);
        
        // แสดงทิศทางการเดินด้วยลูกศรใหญ่
        if (pedestrian.isCrossing()) {
            g2d.setColor(new Color(0, 200, 0)); // สีเขียวสด
            g2d.setStroke(new BasicStroke((float)(0.4 * scaleFactor), 
                                         BasicStroke.CAP_ROUND, 
                                         BasicStroke.JOIN_ROUND));
            
            // วาดลูกศรแสดงทิศทาง
            double arrowSize = radius * 0.8;
            double arrowX = centerX;
            double arrowY = centerY + radius/2;
            
            // เส้นลูกศร
            g2d.drawLine(
                (int)arrowX, 
                (int)arrowY, 
                (int)arrowX, 
                (int)(arrowY + arrowSize)
            );
            
            // หัวลูกศร
            int[] xPoints = {
                (int)arrowX,
                (int)(arrowX - arrowSize * 0.3),
                (int)(arrowX + arrowSize * 0.3)
            };
            int[] yPoints = {
                (int)(arrowY + arrowSize),
                (int)(arrowY + arrowSize * 0.7),
                (int)(arrowY + arrowSize * 0.7)
            };
            g2d.fillPolygon(xPoints, yPoints, 3);
        }
    }
}
