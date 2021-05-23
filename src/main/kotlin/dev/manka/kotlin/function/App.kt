package dev.manka.kotlin.function

import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions
import com.google.cloud.functions.HttpFunction
import com.google.cloud.functions.HttpRequest
import com.google.cloud.functions.HttpResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import mu.KotlinLogging
import java.io.IOException
import java.util.concurrent.TimeUnit


private val logger = KotlinLogging.logger {}

class App : HttpFunction {
    private val firestore: Firestore = FirestoreOptions.getDefaultInstance().service

    @Throws(IOException::class)
    override fun service(request: HttpRequest, response: HttpResponse) {

        when (request.method) {
            "PUT" -> createOrUpdateDocument(request, response)
            "GET" -> writeDocumentToResponse(request, response)
            else -> {
                response.writer.write("${request.method} is not supported, only PUT & GET are supported")
                response.setStatusCode(405)
                response.appendHeader("Allow", "PUT , GET")
            }
        }
    }

    private fun writeDocumentToResponse(request: HttpRequest, response: HttpResponse) {
        val documentPath = request.queryParameters["documentPath"]
        if (documentPath.isNullOrEmpty()) {
            response.writer.write("""<h1> Welcome to gcloud function in Kotlin </h1> 
                use GET method with parameter documentPath to get document for example ?documentPath=testCollection/test and 
                PUT method to create or update document """)

        } else {
            logger.info("getting data from ${documentPath[0]}")
            val content = firestore.document(documentPath[0]).get().get(1,TimeUnit.SECONDS)
            logger.info { content }
            val user = content.toObject(UserData::class.java)
            logger.info { user }
            response.writer.write(user.toString())
        }
    }

    private fun createOrUpdateDocument(request: HttpRequest, response: HttpResponse) {
        val jsonObject = JsonParser.parseReader(request.reader).asJsonObject
        val documentPath = jsonObject.get("documentPath").asString
        val content = jsonObject.get("content").asJsonObject

        logger.info { content.toString() }
        if (documentPath != null && content != null) {
            firestore.document(documentPath).set(toUserData(content))
            response.writer.write("successfully created document $documentPath")
        } else {
            response.setStatusCode(422, "missing documentPath or content in json")

        }
    }

    private fun toUserData(json: JsonObject): UserData {
        val gson = Gson()
        return gson.fromJson(json,UserData::class.java)
    }

}