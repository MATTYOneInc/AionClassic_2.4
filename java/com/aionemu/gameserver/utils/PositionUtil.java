package com.aionemu.gameserver.utils;

import com.aionemu.gameserver.model.gameobjects.VisibleObject;

public class PositionUtil
{
	private static final float MAX_ANGLE_DIFF = 90f;
	
    public static boolean isBehindTarget(VisibleObject object1, VisibleObject object2) {
        float angleObject2;
        float angleObject1 = MathUtil.calculateAngleFrom(object1, object2);
        float angleDiff = angleObject1 - (angleObject2 = MathUtil.convertHeadingToDegree(object2.getHeading()));
        if (angleDiff <= -270.0f) {
            angleDiff += 360.0f;
        } if (angleDiff >= 270.0f) {
            angleDiff -= 360.0f;
        }
        return Math.abs(angleDiff) <= 90.0f;
    }
	
    public static boolean isInFrontOfTarget(VisibleObject object1, VisibleObject object2) {
        float angleObject2 = MathUtil.calculateAngleFrom(object2, object1);
        float angleObject1 = MathUtil.convertHeadingToDegree(object2.getHeading());
        float angleDiff = angleObject1 - angleObject2;
        if (angleDiff <= -270.0f) {
            angleDiff += 360.0f;
        } if (angleDiff >= 270.0f) {
            angleDiff -= 360.0f;
        }
        return Math.abs(angleDiff) <= 90.0f;
    }
	
    public static boolean isBehind(VisibleObject object1, VisibleObject object2) {
        float angle = MathUtil.convertHeadingToDegree(object1.getHeading()) + 90.0f;
        if (angle >= 360.0f) {
            angle -= 360.0f;
        }
        double radian = Math.toRadians(angle);
        float x0 = object1.getX();
        float y0 = object1.getY();
        float x1 = (float)(Math.cos(radian) * 5.0) + x0;
        float y1 = (float)(Math.sin(radian) * 5.0) + y0;
        float xA = object2.getX();
        float yA = object2.getY();
        float temp = (x1 - x0) * (yA - y0) - (y1 - y0) * (xA - x0);
        return temp > 0.0f;
    }
	
    public static float getAngleToTarget(VisibleObject object1, VisibleObject object2) {
        float angleObject2;
        float angleDiff;
        float angleObject1 = MathUtil.convertHeadingToDegree(object1.getHeading()) - 180.0f;
        if (angleObject1 < 0.0f) {
            angleObject1 += 360.0f;
        } if ((angleDiff = angleObject1 - (angleObject2 = MathUtil.calculateAngleFrom(object1, object2)) - 180.0f) < 0.0f) {
            angleDiff += 360.0f;
        }
        return angleDiff;
    }
	
    public static float getDirectionalBound(VisibleObject object1, VisibleObject object2, boolean inverseTarget) {
        float angle = 90.0f - (inverseTarget ? PositionUtil.getAngleToTarget(object2, object1) : PositionUtil.getAngleToTarget(object1, object2));
        if (angle < 0.0f) {
            angle += 360.0f;
        }
        double radians = Math.toRadians(angle);
        float x1 = (float)((double)object1.getX() + (double)object1.getObjectTemplate().getBoundRadius().getSide() * Math.cos(radians));
        float y1 = (float)((double)object1.getY() + (double)object1.getObjectTemplate().getBoundRadius().getFront() * Math.sin(radians));
        float x2 = (float)((double)object2.getX() + (double)object2.getObjectTemplate().getBoundRadius().getSide() * Math.cos(Math.PI + radians));
        float y2 = (float)((double)object2.getY() + (double)object2.getObjectTemplate().getBoundRadius().getFront() * Math.sin(Math.PI + radians));
        float bound1 = (float)MathUtil.getDistance(object1.getX(), object1.getY(), x1, y1);
        float bound2 = (float)MathUtil.getDistance(object2.getX(), object2.getY(), x2, y2);
        return bound1 - bound2;
    }
	
    public static float getDirectionalBound(VisibleObject object1, VisibleObject object2) {
        return PositionUtil.getDirectionalBound(object1, object2, false);
    }
	
    public static byte getMoveAwayHeading(VisibleObject fromObject, VisibleObject object) {
        float angle = MathUtil.calculateAngleFrom(fromObject, object);
        byte heading = MathUtil.convertDegreeToHeading(angle);
        return heading;
    }
}