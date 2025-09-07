import dev.johnoreilly.common.di.initKoin
import dev.johnoreilly.common.repository.PeopleInSpaceRepository
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.utils.io.streams.*
import io.modelcontextprotocol.kotlin.sdk.*
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.StdioServerTransport
import io.modelcontextprotocol.kotlin.sdk.server.mcp
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.io.asSink
import kotlinx.io.buffered


private val koin = initKoin(enableNetworkLogs = true).koin

fun configureServer(): Server {
    val peopleInSpaceRepository = koin.get<PeopleInSpaceRepository>()

    val server = Server(
        Implementation(
            name = "mcp-kotlin PeopleInSpace server",
            version = "1.0.0"
        ),
        ServerOptions(
            capabilities = ServerCapabilities(
                prompts = ServerCapabilities.Prompts(listChanged = true),
                resources = ServerCapabilities.Resources(subscribe = true, listChanged = true),
                tools = ServerCapabilities.Tools(listChanged = true)
            )
        )
    )


    server.addTool(
        name = "get-people-in-space",
        description = "The list of people in space endpoint returns the list of people in space right now"
    ) {
        val people = peopleInSpaceRepository.fetchPeople()
        CallToolResult(
            content =
                people.map { TextContent(it.name) }
        )
    }

    return server
}

/**
 * Runs an MCP (Model Context Protocol) server using standard I/O for communication.
 *
 * This function initializes a server instance configured with predefined tools and capabilities.
 * It sets up a transport mechanism using standard input and output for communication.
 * Once the server starts, it listens for incoming connections, processes requests,
 * and executes the appropriate tools. The server shuts down gracefully upon receiving
 * a close event.
 */
fun `run mcp server using stdio`() {
    val server = configureServer()
    val transport = StdioServerTransport(
        System.`in`.asInput(),
        System.out.asSink().buffered()
    )

    runBlocking {
        server.connect(transport)
        val done = Job()
        server.onClose {
            done.complete()
        }
        done.join()
    }
}

/**
 * Launches an SSE (Server-Sent Events) MCP (Model Context Protocol) server on the specified port.
 * This server enables clients to connect via SSE for real-time communication and provides endpoints
 * for handling specific messages.
 *
 * @param port The port number on which the SSE server should be started.
 */
fun `run sse mcp server`(port: Int): Unit = runBlocking {
    val server = configureServer()
    embeddedServer(CIO, host = "0.0.0.0", port = port) {
        mcp {
            server
        }
    }.start(wait = true)
}
