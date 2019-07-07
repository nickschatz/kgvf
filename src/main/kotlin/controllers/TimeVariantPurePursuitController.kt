package controllers

import motionprofile.MotionProfile
import motionprofile.TrapezoidalMotionProfile
import paths.Path
import kotlin.math.pow
import Pose
import SkidSteerCommand
import Vector2D
import kotlin.math.min

class TimeVariantPurePursuitController(path: Path, val lookahead: Double, val limits: MotionProfile.Limits): TimeVariantPathController(path) {
    val motionProfile = TrapezoidalMotionProfile(path.length(), limits);
    override fun curvatureControl(pose: Pose, time: Double): SkidSteerCommand {
        val t = motionProfile.getPosition(time) / path.length()
        val speed = motionProfile.getSpeed(time)

        val t_ahead = min(1.0, (motionProfile.getPosition(time) + lookahead) / path.length())
        val ahead_pos = pose.translate(path.calculatePoint(t_ahead))
        val curvature = -2 * ahead_pos.y / ahead_pos.norm()
        return SkidSteerCommand(speed, curvature)
    }
    override fun vectorControl(pose: Pose, time: Double): Vector2D {
        val t = motionProfile.getPosition(time) / path.length()
        val speed = motionProfile.getSpeed(time)

        val t_ahead = min(1.0, (motionProfile.getPosition(time) + lookahead) / path.length())
        val ahead_pos = path.calculatePoint(t_ahead)

        return (ahead_pos - pose).normalized().scalarMul(speed)
    }
}