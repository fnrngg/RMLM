package ge.custom.rmlm.presenatation.viewmodels

import android.net.Uri
import ge.custom.rmlm.base.TestCoroutinesRule
import ge.custom.rmlm.common.Result
import ge.custom.rmlm.domain.model.RecordingData
import ge.custom.rmlm.domain.usecase.DeleteRecordingUseCase
import ge.custom.rmlm.presenatation.mapper.RecordingMapper
import ge.custom.rmlm.presenatation.model.RecordingUiData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Locale
import java.util.TimeZone

class RecordingsViewModelTest {

    @get:Rule
    val mainCoroutineRule = TestCoroutinesRule()

    private lateinit var viewModel: RecordingsViewModel
    private val mapper = RecordingMapper(mockk {
        every { getCurrentLocale() } returns Locale.ENGLISH
        every { getCurrentTimeZone() } returns TimeZone.getTimeZone("UTC")
    })
    val mockedUri = mockk<Uri>()
    private val testSearchData = listOf(
        RecordingData(
            "test",
            mockedUri,
            1000L,
            1000L
        )
    )
    private val deleteRecordingUseCase = mockk<DeleteRecordingUseCase> {
        coEvery { this@mockk(mockedUri) } returns Result.Success(Unit)
    }
    private val testSearchUiData = testSearchData.map {
        mapper.mapRecordingDataToRecordingUiData(it)
    }

    @Before
    fun setup() {
        viewModel = RecordingsViewModel(
            mockk {
                coEvery { this@mockk("") } returns Result.Success(emptyList())
                coEvery { this@mockk("test") } returns Result.Success(
                    testSearchData
                )
            },
            deleteRecordingUseCase,
            mapper
        )
    }

    @Test
    fun `initial state should be empty search and recordings, false showDeleteDialog and null chosenRecordingUri`() =
        runTest {
            assert(viewModel.recordingsUiState.value.search.isEmpty())
            assert(!viewModel.recordingsUiState.value.showDeleteDialog)
            assert(viewModel.recordingsUiState.value.chosenRecordingUri == null)
            assert(viewModel.recordingsUiState.value.recordings is Result.Loading)
            delay(600L)
            val initialResult = Result.Success<List<RecordingUiData>>(emptyList())
            assert(viewModel.recordingsUiState.value.recordings == initialResult)
        }

    @Test
    fun `search should update search value and load recordings`() = runTest {
        assert(viewModel.recordingsUiState.value.search.isEmpty())
        viewModel.search("test")
        assert(viewModel.recordingsUiState.value.search == "test")
        assert(viewModel.recordingsUiState.value.recordings is Result.Loading)
        delay(600L)
        val searchTestResult = Result.Success(
            testSearchUiData
        )
        assert(viewModel.recordingsUiState.value.recordings == searchTestResult)
    }

    @Test
    fun `deleteRecording should update showDeleteDialog and chosenRecordingUri`() = runTest {
        viewModel.deleteRecording(mockedUri)
        assert(viewModel.recordingsUiState.value.showDeleteDialog)
        assert(viewModel.recordingsUiState.value.chosenRecordingUri == mockedUri)
    }

    @Test
    fun `deleteAgreed should call deleteRecordingUseCase, update showDeleteDialog and chosenRecordingUri to null`() = runTest {
        viewModel.deleteRecording(mockedUri)
        viewModel.deleteAgreed()
        coVerify { deleteRecordingUseCase(mockedUri) }
        assert(!viewModel.recordingsUiState.value.showDeleteDialog)
        assert(viewModel.recordingsUiState.value.chosenRecordingUri == null)
    }
}