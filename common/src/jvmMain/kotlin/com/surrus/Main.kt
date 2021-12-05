package com.surrus

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.*
import com.surrus.common.di.initKoin
import com.surrus.common.remote.PeopleInSpaceApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {

    val koin = initKoin(enableNetworkLogs = true).koin
    val peopleInSpace = koin.get<PeopleInSpaceApi>()


    val dynamoDbClient = DynamoDbClient { region = "eu-west-1" }

    val table = "PeopleInSpace"
    val key = "id"

    try {
        setupDynamoDb(dynamoDbClient, table, key)

        println("Writing to table...")

        val people = peopleInSpace.fetchPeople().people

        people.forEach { person ->
            val itemValues = mutableMapOf<String, AttributeValue>()
            itemValues["id"] = AttributeValue.S(person.name)
            itemValues["craft"] =  AttributeValue.S(person.craft)
            itemValues["personImageUrl"] =  AttributeValue.S(person.personImageUrl!!)

            dynamoDbClient.putItem {
                tableName = table
                item = itemValues
            }
        }


        println("Completed writing to table.")
        println()

        //cleanUp(dynamoDbClient, table)

    } catch (e: DynamoDbException) {
        println("ERROR (DynamoDbException): " + e.message)
    } finally {
        dynamoDbClient.close()
    }
    println("Exiting...")


}


private suspend fun setupDynamoDb(dynamoDbClient: DynamoDbClient, newTable: String, key: String) {

    val createTableRequest = CreateTableRequest {
        tableName = newTable
        attributeDefinitions = listOf(
            AttributeDefinition {
                attributeName = key
                attributeType = ScalarAttributeType.S
            }
        )
        keySchema = listOf(
            KeySchemaElement {
                attributeName = key
                keyType = KeyType.Hash
            }
        )
        provisionedThroughput {
            readCapacityUnits = 10
            writeCapacityUnits = 10
        }
    }
    println("Creating table: $newTable...")
    dynamoDbClient.createTable(createTableRequest)
    println("Waiting for table to be active...")
    var tableIsActive = dynamoDbClient.describeTable {
        tableName = newTable
    }.table?.tableStatus == TableStatus.Active
    do {
        if (!tableIsActive) {
            delay(500)
            tableIsActive = dynamoDbClient.describeTable {
                tableName = newTable
            }.table?.tableStatus == TableStatus.Active
        }
    } while(!tableIsActive)
    println("$newTable is ready.")
    println()
}

private suspend fun cleanUp(dynamoDbClient: DynamoDbClient, newTable: String) {
    println("Cleaning up...")
    println("Deleting table: $newTable...")
    dynamoDbClient.deleteTable {
        tableName = newTable
    }
    println("$newTable has been deleted.")
    println()
    println("Cleanup complete")
    println()
}