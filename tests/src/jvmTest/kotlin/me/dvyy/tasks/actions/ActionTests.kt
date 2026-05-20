package me.dvyy.tasks.actions

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.test.runTest
import me.dvyy.tasks.helpers.ClientServerHelpers
import me.dvyy.tasks.model.components.ProjectModel
import me.dvyy.tasks.model.components.TaskModel
import org.junit.jupiter.api.Test
import kotlin.uuid.ExperimentalUuidApi

class ActionTests {
    val app = ClientServerHelpers()

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `should be able to run common actions`() = runTest {
        app.syncClient.initialize()
        val mutate = app.client.mutate
        val projectA = mutate.projects.create(ProjectModel("Project A"))
        val projectB = mutate.projects.create(ProjectModel("Project B"))
        val taskA = mutate.tasks.create(TaskModel("Hello"), projectA)
        val taskB = mutate.tasks.create(TaskModel("Hello 2"), projectA)
        mutate.childOf.move(taskA, projectB)
        mutate.childOf.move(taskB, projectB, atEnd = true)

        app.clientDb.read {
            val childrenA = app.client.query.childOf.childrenOf(projectA)
            val childrenB = app.client.query.childOf.childrenOf(projectB)
            childrenA.shouldBeEmpty()
            childrenB shouldContainExactly listOf(taskA, taskB)
        }
    }
}