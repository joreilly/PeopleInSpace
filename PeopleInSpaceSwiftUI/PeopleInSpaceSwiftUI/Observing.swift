import SwiftUI
import KotlinCoroutineSupport

/// Replacement for SKIE's `Observing`, built on Swift Export's typed StateFlow.
///
/// Seeds from `flow.value` so the first frame is never empty, then rebuilds on
/// each emission from `asAsyncSequence()`. `.task` ties the collection to the
/// view's lifetime, so the Kotlin flow is cancelled when the view goes away.
struct Observing<Value, Content: View>: View {
    private let flow: any KotlinTypedStateFlow<Value>
    private let content: (Value) -> Content

    @State private var value: Value

    init(
        _ flow: any KotlinTypedStateFlow<Value>,
        @ViewBuilder content: @escaping (Value) -> Content
    ) {
        self.flow = flow
        self.content = content
        self._value = State(initialValue: flow.value)
    }

    var body: some View {
        content(value)
            .task {
                for await next in flow.asAsyncSequence() {
                    value = next
                }
            }
    }
}
