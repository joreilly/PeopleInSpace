import Foundation
import Combine
import common

@MainActor
class PeopleInSpaceViewModel: ObservableObject {
    @Published var people = [Assignment]()
    @Published var issPosition = IssPosition(latitude: 0, longitude: 0)
    
    private var subscription: AnyCancellable?
    
    public let repository: PeopleInSpaceRepository
    init(repository: PeopleInSpaceRepository) {
        self.repository = repository
    }
    

    
    func stopObservingPeopleUpdates() {
        repository.stopObservingPeopleUpdates()
    }
    
    func getPersonBio(personName: String) -> String {
        return repository.getPersonBio(personName: personName)
    }
    
    func getPersonImage(personName: String) -> String {
        return repository.getPersonImage(personName: personName)
    }

    
    func getPeopleAsync() async  {

        do {
            print("before")
            
            let delayMs:     Int64 = 3000
            self.people = try await asyncFunction(for: repository.fetchPeopleNative(delayMs: delayMs))
            print(self.people)
            print("after")
        }
        catch {
            print("Task error: \(error)")
        }
    }
}

    
    

public typealias NativeCallback<T, Unit> = (T, Unit) -> Unit
public typealias NativeCancellable<Unit> = () -> Unit

public typealias NativeSuspend<Result, Failure: Error, Unit> = (
    _ onResult: @escaping NativeCallback<Result, Unit>,
    _ onError: @escaping NativeCallback<Failure, Unit>
) -> NativeCancellable<Unit>



/// Wraps the `NativeSuspend` in an async function.
/// - Parameter nativeSuspend: The native suspend function to await.
/// - Returns: The result from the `nativeSuspend`.
/// - Throws: Errors thrown by the `nativeSuspend`.
public func asyncFunction<Result, Failure: Error, Unit>(for nativeSuspend: @escaping NativeSuspend<Result, Failure, Unit>) async throws -> Result {
    let asyncFunctionActor = AsyncFunctionActor<Result, Unit>()
    return try await withTaskCancellationHandler {
        async { await asyncFunctionActor.cancel() }
    } operation: {
        try await withUnsafeThrowingContinuation { continuation in
            async {
                await asyncFunctionActor.setContinuation(continuation)
                let nativeCancellable = nativeSuspend({ output, unit in
                    async { await asyncFunctionActor.continueWith(result: output) }
                    return unit
                }, { error, unit in
                    async { await asyncFunctionActor.continueWith(error: error) }
                    return unit
                })
                await asyncFunctionActor.setNativeCancellable(nativeCancellable)
            }
        }
    }
}

internal actor AsyncFunctionActor<Result, Unit> {
    
    private var isCancelled = false
    private var nativeCancellable: NativeCancellable<Unit>? = nil
    
    func setNativeCancellable(_ nativeCancellable: @escaping NativeCancellable<Unit>) {
        guard !isCancelled else {
            _ = nativeCancellable()
            return
        }
        self.nativeCancellable = nativeCancellable
    }
    
    func cancel() {
        isCancelled = true
        _ = nativeCancellable?()
        nativeCancellable = nil
    }
    
    private var continuation: UnsafeContinuation<Result, Error>? = nil
    
    func setContinuation(_ continuation: UnsafeContinuation<Result, Error>) {
        self.continuation = continuation
    }
    
    func continueWith(result: Result) {
        continuation?.resume(returning: result)
        continuation = nil
    }
    
    func continueWith(error: Error) {
        continuation?.resume(throwing: error)
        continuation = nil
    }
}
