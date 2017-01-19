package sandbox.physics

import org.dyn4j.collision.Bounds
import org.dyn4j.dynamics.Body
import org.dyn4j.dynamics.BodyFixture
import org.dyn4j.dynamics.World
import org.dyn4j.geometry.*
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL11.*


/**
 * Created by kyle on 1/16/17.
 */
fun world(bounds: Bounds, init: GameWorld.() -> Unit): GameWorld {
    val world: GameWorld = GameWorld(bounds)
    world.init()
    return world
}

fun GameWorld.demo() {
    val floorRect = Rectangle(15.0, 1.0)
    val floor = GameObject()
    floor.addFixture(BodyFixture(floorRect))
    floor.setMass(MassType.INFINITE)
    // move the floor down a bit
    floor.translate(0.0, -4.0)
    this.addBody(floor)

    // create a triangle object
    val triShape = Triangle(
            Vector2(0.0, 0.5),
            Vector2(-0.5, -0.5),
            Vector2(0.5, -0.5))
    val triangle = GameObject()
    triangle.addFixture(triShape)
    triangle.setMass(MassType.NORMAL)
    triangle.translate(-1.0, 2.0)
    // test having a velocity
    triangle.getLinearVelocity().set(5.0, 0.0)
    this.addBody(triangle)

    // create a circle
    val cirShape = Circle(0.5)
    val circle = GameObject()
    circle.addFixture(cirShape)
    circle.setMass(MassType.NORMAL)
    circle.translate(2.0, 2.0)
    // test adding some force
    circle.applyForce(Vector2(-100.0, 0.0))
    // set some linear damping to simulate rolling friction
    circle.setLinearDamping(0.05)
    this.addBody(circle)

    // try a rectangle
    val rectShape = Rectangle(1.0, 1.0)
    val rectangle = GameObject()
    rectangle.addFixture(rectShape)
    rectangle.setMass(MassType.NORMAL)
    rectangle.translate(0.0, 2.0)
    rectangle.getLinearVelocity().set(-5.0, 0.0)
    this.addBody(rectangle)

    // try a polygon with lots of vertices
    val polyShape = Geometry.createUnitCirclePolygon(10, 1.0)
    val polygon = GameObject()
    polygon.addFixture(polyShape)
    polygon.setMass(MassType.NORMAL)
    polygon.translate(-2.5, 2.0)
    // set the angular velocity
    polygon.setAngularVelocity(Math.toRadians(-20.0))
    this.addBody(polygon)

    // try a compound object
    val c1 = Circle(0.5)
    val c1Fixture = BodyFixture(c1)
    c1Fixture.density = 0.5
    val c2 = Circle(0.5)
    val c2Fixture = BodyFixture(c2)
    c2Fixture.density = 0.5
    val rm = Rectangle(2.0, 1.0)
    // translate the circles in local coordinates
    c1.translate(-1.0, 0.0)
    c2.translate(1.0, 0.0)
    val capsule = GameObject()
    capsule.addFixture(c1Fixture)
    capsule.addFixture(c2Fixture)
    capsule.addFixture(rm)
    capsule.setMass(MassType.NORMAL)
    capsule.translate(0.0, 4.0)
    this.addBody(capsule)

    val issTri = GameObject()
    issTri.addFixture(Geometry.createIsoscelesTriangle(1.0, 3.0))
    issTri.setMass(MassType.NORMAL)
    issTri.translate(2.0, 3.0)
    this.addBody(issTri)

    val equTri = GameObject()
    equTri.addFixture(Geometry.createEquilateralTriangle(2.0))
    equTri.setMass(MassType.NORMAL)
    equTri.translate(3.0, 3.0)
    this.addBody(equTri)

    val rightTri = GameObject()
    rightTri.addFixture(Geometry.createRightTriangle(2.0, 1.0))
    rightTri.setMass(MassType.NORMAL)
    rightTri.translate(4.0, 3.0)
    this.addBody(rightTri)

    val cap = GameObject()
    cap.addFixture(Capsule(1.0, 0.5))
    cap.setMass(MassType.NORMAL)
    cap.translate(-3.0, 3.0)
    this.addBody(cap)

    val slice = GameObject()
    slice.addFixture(Slice(0.5, Math.toRadians(120.0)))
    slice.setMass(MassType.NORMAL)
    slice.translate(-3.0, 3.0)
    this.addBody(slice)
}

