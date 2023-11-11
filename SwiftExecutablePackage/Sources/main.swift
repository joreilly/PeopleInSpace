import KMPNativeCoroutinesAsync
import PeopleInSpaceKit

KoinKt.doInitKoin()
let repository = PeopleInSpaceRepository()
let people = try await asyncFunction(for: repository.fetchPeople())
people.forEach { person in
    print(person.name)
}

let issPositionStream = asyncSequence(for: repository.pollISSPosition())
for try await issPosition in issPositionStream {
    print(issPosition)
}
