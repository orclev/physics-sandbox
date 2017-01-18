package sandbox.graphics

import kotlinx.support.jdk7.use
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GLCapabilities
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil

/**
 * Created by kyle on 1/16/17.
 */
class Renderer<T>(val width: Int, val height: Int, var state: T) : AutoCloseable {
    val window: Long by lazy {
        GLFW.glfwCreateWindow(width, height, "Hello World!", MemoryUtil.NULL, MemoryUtil.NULL)
    }
    var lastRender: Double = 0.0
    init {
        GLFWErrorCallback.createPrint(System.err).set()
        if ( !GLFW.glfwInit()) {
            throw IllegalStateException("Unable to initialize GLFW")
        }
        GLFW.glfwDefaultWindowHints()
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE)
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE)
        if ( window == MemoryUtil.NULL) {
            throw RuntimeException("Failed to create the GLFW window")
        }

        GLFW.glfwSetKeyCallback(window) { window, key, scancode, action, mods ->
            if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE)
                GLFW.glfwSetWindowShouldClose(window, true) // We will detect this in the rendering loop
        }

        // Get the thread stack and push a new frame
        MemoryStack.stackPush().use { stack: MemoryStack ->
            val pWidth = stack.mallocInt(1) // int*
            val pHeight = stack.mallocInt(1) // int*

            // Get the window size passed to glfwCreateWindow
            GLFW.glfwGetWindowSize(window, pWidth, pHeight)

            // Get the resolution of the primary monitor
            val vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())

            // Center the window
            GLFW.glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            )
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        GLFW.glfwMakeContextCurrent(window)
        // Enable v-sync
        GLFW.glfwSwapInterval(1)

        // Make the window visible
        GLFW.glfwShowWindow(window)
        GL.createCapabilities()
    }

    fun loop(render: Renderer<T>.(dt: Double) -> Unit) {
        // Set the clear color
        glMatrixMode(GL_PROJECTION)
        glLoadIdentity()
        glOrtho(-400.0,400.0,-300.0,300.0,0.0,1.0)

        glMatrixMode(GL_MODELVIEW)
        glLoadIdentity()
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f)

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        GLFW.glfwSetTime(0.0)
        lastRender = 0.0
        while (!GLFW.glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT) // clear the framebuffer

            glMatrixMode(GL_MODELVIEW)
            glLoadIdentity()

            val now = GLFW.glfwGetTime()
            render(now - lastRender)
            lastRender = now

            GLFW.glfwSwapBuffers(window) // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            GLFW.glfwPollEvents()
        }
    }

    override fun close() {
        Callbacks.glfwFreeCallbacks(window)
        GLFW.glfwDestroyWindow(window)
        GLFW.glfwTerminate()
        GLFW.glfwSetErrorCallback(null).free()
    }

}
