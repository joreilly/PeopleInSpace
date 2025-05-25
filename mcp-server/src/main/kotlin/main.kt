/**
 * Entry point.
 * It initializes and runs the appropriate server mode based on the input arguments.
 *
 * Command-line arguments passed to the application:
 * - args[0]: Specifies the server mode. Supported values are:
 *      - "--sse-server": Runs the SSE MCP server.
 *      - "--stdio": Runs the MCP server using standard input/output.
 *      Defaults to "--sse-server" if not provided.
 * - args[1]: Specifies the port number for the server. Defaults to 3001 if not provided or invalid.
 */


fun main(args: Array<String>) {
    val command = args.firstOrNull() ?: "--sse-server"
    val port = args.getOrNull(1)?.toIntOrNull() ?: 3001
    when (command) {
        "--sse-server" -> `run sse mcp server`(port)
        "--stdio" -> `run mcp server using stdio`()
        else -> {
            System.err.println("Unknown command: $command")
        }
    }
}