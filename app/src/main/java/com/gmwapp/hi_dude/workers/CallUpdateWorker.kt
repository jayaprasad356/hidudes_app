package com.gmwapp.hi_dude.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gmwapp.hi_dude.constants.DConstants
import com.gmwapp.hi_dude.repositories.FemaleUsersRepositories
import com.gmwapp.hi_dude.retrofit.responses.UpdateConnectedCallResponse
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

@HiltWorker
class CallUpdateWorker @AssistedInject constructor(
    val femaleUsersRepositories: FemaleUsersRepositories,
    @Assisted appContext: Context,
    @Assisted val workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                var updateConnectedCall: Response<UpdateConnectedCallResponse>? = null
                if (workerParams.inputData.getBoolean(DConstants.IS_INDIVIDUAL, false)) {
                    updateConnectedCall = femaleUsersRepositories.individualUpdateConnectedCall(
                        workerParams.inputData.getInt(DConstants.USER_ID, 0),
                        workerParams.inputData.getInt(DConstants.CALL_ID, 0),
                        workerParams.inputData.getString(DConstants.STARTED_TIME).toString(),
                        workerParams.inputData.getString(DConstants.ENDED_TIME).toString(),
                    )
                } else {
                    updateConnectedCall = femaleUsersRepositories.updateConnectedCall(
                        workerParams.inputData.getInt(DConstants.USER_ID, 0),
                        workerParams.inputData.getInt(DConstants.CALL_ID, 0),
                        workerParams.inputData.getString(DConstants.STARTED_TIME).toString(),
                        workerParams.inputData.getString(DConstants.ENDED_TIME).toString(),
                    )
                }

                if (updateConnectedCall.isSuccessful == true) {
                    if (updateConnectedCall.body()?.success == true) {
                        Result.success()
                    } else {
                        Result.failure()
                    }
                } else {
                    Result.failure()
                }
            } catch (e: Exception) {
                Result.failure()
            }

        }
    }
}
