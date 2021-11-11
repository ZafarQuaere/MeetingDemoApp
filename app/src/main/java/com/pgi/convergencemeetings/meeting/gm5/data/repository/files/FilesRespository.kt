package com.pgi.convergencemeetings.meeting.gm5.data.repository.files

import com.pgi.network.helper.RetryAfterTimeoutWithDelay
import com.pgi.network.repository.BaseRepository
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

class FilesRespository private constructor() : BaseRepository() {
	private object Holder {
		val instance = FilesRespository()
	}

	companion object {
		val instance: FilesRespository by lazy {
			Holder.instance
		}
	}

	var fileService: FileApi? = null

	private fun getFilesService(): FileApi {
		return if (fileService == null) {
			fileService = FilesManager.create()
			fileService!!
		} else {
			fileService!!
		}
	}

	fun createSession(contentid: String): Observable<Response<Void>> {
		return getFilesService().createSession(contentid)
				.subscribeOn(Schedulers.io())
				.unsubscribeOn(Schedulers.io())
				.retryWhen(RetryAfterTimeoutWithDelay(maxRetries, retryTimeout))
	}
}

interface FileApi {
	@GET("/files/{contentid}/presentation/session")
	fun createSession(@Path("contentid") contentid: String): Observable<Response<Void>>
}