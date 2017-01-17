package sandbox.physics

import org.dyn4j.collision.Bounds
import org.dyn4j.dynamics.Body
import org.dyn4j.dynamics.BodyFixture
import org.dyn4j.dynamics.World
import org.dyn4j.geometry.*
import org.lwjgl.opengl.GL11.*




/**
 * Created by kyle on 1/16/17.
 */
fun world(bounds: Bounds, init: GameWorld.() -> Unit): GameWorld {
    val world: GameWorld = GameWorld(bounds)
    world.init()
    return world
}

class GameObject : Body() {
    /** The color of the object  */
    protected var color = FloatArray(4)

    /**
     * Default constructor.
     */
    init {
        // randomly generate the color
        this.color[0] = Math.random().toFloat() * 0.5f + 0.5f
        this.color[1] = Math.random().toFloat() * 0.5f + 0.5f
        this.color[2] = Math.random().toFloat() * 0.5f + 0.5f
        this.color[3] = 1.0f
    }

    /**
     * Draws the body.
     *
     *
     * Only coded for polygons.
     * @param gl the OpenGL graphics context
     */
    fun render() {
        // save the original transform
        glPushMatrix()

        // transform the coordinate system from world coordinates to local coordinates
        glTranslated(this.transform.translationX, this.transform.translationY, 0.0)
        // rotate about the z-axis
        glRotated(Math.toDegrees(this.transform.rotation), 0.0, 0.0, 1.0)

        // loop over all the body fixtures for this body
        for (fixture in this.fixtures) {
            // get the shape on the fixture
            val convex = fixture.shape
            // check the shape type
            if (convex is Polygon) {
                // since Triangle, Rectangle, and Polygon are all of
                // type Polygon in addition to their main type
                // set the color
                glColor4fv(this.color)

                // fill the shape
                glBegin(GL_POLYGON)
                for (v in convex.vertices) {
                    glVertex3d(v.x, v.y, 0.0)
                }
                glEnd()

                // set the color
                glColor4f(this.color[0] * 0.8f, this.color[1] * 0.8f, this.color[2] * 0.8f, 1.0f)

                // draw the shape
                glBegin(GL_LINE_LOOP)
                for (v in convex.vertices) {
                    glVertex3d(v.x, v.y, 0.0)
                }
                glEnd()
            }
            // circles and other curved shapes require a little more work, so to keep
            // this example short we only include polygon shapes; see the RenderUtilities
            // in the Sandbox application
        }

        // set the original transform
        glPopMatrix()
    }
}

fun GameWorld.floor() {
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

class GameWorld(bounds: Bounds) : World(bounds) {
    /** The scale 45 pixels per meter  */
    val SCALE = 45.0

    /** The conversion factor from nano to base  */
    val NANO_TO_BASE = 1.0e9

    fun render() {
        // apply a scaling transformation
        glScaled(SCALE, SCALE, SCALE)

        // lets move the view up some
        glTranslated(0.0, -1.0, 0.0)

        // draw all the objects in the world
        for (i in 0..this.getBodyCount() - 1) {
            // get the object
            val body: GameObject = getBody(i) as GameObject
            // draw the object
            body.render()
        }
    }
}
