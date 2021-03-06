package paths

import math.Vector2D
import org.apache.commons.math3.linear.Array2DRowRealMatrix
import org.apache.commons.math3.linear.RealMatrix
import java.lang.Math.atan
import kotlin.math.pow

abstract class Path {
    abstract fun closestTOnPathTo(r: Vector2D, guess: Double): Double
    abstract fun calculatePoint(t: Double): Vector2D
    abstract fun tangentVec(t: Double): Vector2D
    abstract fun levelSet(r: Vector2D, closestT: Double): Double
    abstract fun errorGradient(r: Vector2D, closestT: Double): Vector2D
    open fun error(s: Double): Double {
        return atan(s)
    }
    open fun errorDeriv(s: Double): Double {
        return 1.0 / (1.0 + s.pow(2))
    }
    open fun hessian(r: Vector2D, closestT: Double): RealMatrix {
        // Discretely calculate the hessian because lol
        val d = 0.001
        val x1 = this.levelSet(r - Vector2D(d, 0.0), closestT)
        val x2 = this.levelSet(r, closestT)
        val x3 = this.levelSet(r + Vector2D(d, 0.0), closestT)

        val dx1 = (x2 - x1) / d
        val dx2 = (x3 - x2) / d

        val ddx = (dx2 - dx1) / d

        val y1 = this.levelSet(r - Vector2D(0.0, d), closestT)
        val y2 = x2
        val y3 = this.levelSet(r + Vector2D(0.0, d), closestT)

        val dy1 = (y2 - y1) / d
        val dy2 = (y3 - y2) / d

        val ddy = (dy2 - dy1) / d

        val xy1 = this.levelSet(r + Vector2D(-d / 2, -d / 2), closestT)
        val xy2 = this.levelSet(r + Vector2D(d / 2, -d / 2), closestT)
        val xy3 = this.levelSet(r + Vector2D(-d / 2, d / 2), closestT)
        val xy4 = this.levelSet(r + Vector2D(d / 2, d / 2), closestT)

        val dydx = ((xy4 - xy3) / d - (xy2 - xy1) / d) / d

        return Array2DRowRealMatrix(arrayOf(
                doubleArrayOf(ddx, dydx),
                doubleArrayOf(dydx, ddy)))

    }
    abstract fun nVec(r: Vector2D, closestT: Double): Vector2D
    abstract fun length(): Double
}