# GCP Kotlin function
Repo for GCP function examples written in Kotlin.
List of examples
| branch                                                                                                 | what               |
|--------------------------------------------------------------------------------------------------------|--------------------|
| [master](https://github.com/matios13/GCP-function-kotlin-examples/tree/master)                         | Always newest one  |
| [basic](https://github.com/matios13/GCP-function-kotlin-examples/tree/basic)                           | Simple hello world |
| [firestore-function](https://github.com/matios13/GCP-function-kotlin-examples/tree/firestore-function) | Firestore function |

More coming soon


## How to build and test locally
In this project i have used gradle written in kotlin if you want to change group you can do it in 
[build.gradle.kts line 9](https://github.com/matios13/GCP-function-kotlin-examples/blob/1922f1c1b5f223ce2f039b11dfb2a78e616e2e56/build.gradle.kts#L9)
If you want to change main class change mainclass in
[build.gradle.kts line 13](https://github.com/matios13/GCP-function-kotlin-examples/blob/1922f1c1b5f223ce2f039b11dfb2a78e616e2e56/build.gradle.kts#L13)
If you want to use older gradle you need to set `mainClassName=AppKt`

**remember to add "Kt" at the end of class name for example for class App you have to use AppKt**

### Testing locally
You can run `./gradlew runFunction`
and enter [localhost:8080](http://localhost:8080/)

If you are using firebase you have to create credentials first:

Replace PROJECT_ID with your project id AND
Replace KEY_PATH with the path of the JSON file that contains your service account key.
```bash
 gcloud iam service-accounts create github-function
 gcloud projects add-iam-policy-binding PROJECT_ID --member="serviceAccount:github-function@PROJECT_ID.iam.gserviceaccount.com" --role="roles/owner"
 gcloud iam service-accounts keys create credentials.json --iam-account=github-function@PROJECT_ID.iam.gserviceaccount.com
 export GOOGLE_APPLICATION_CREDENTIALS="KEY_PATH"
 
```

## How to deploy on GCP:
1. [install gcloud cli and authenticate](https://cloud.google.com/sdk/gcloud)  
1. build function
   ```bash
   ./gradlew buildFunction
   ```
1. chose your project you can list it with
   ```bash
   gcloud projects list
   ``` 
   Then set it with
   ```bash
   gcloud config set project {PROJECT-NAME}
   ```
1. chose region (in example I've used Warsaw)
   ```bash
   gcloud config set functions/region europe-central2
   ```
1. Enable gcloud services for cloud functions and cloud build
    ```bash
    gcloud services enable cloudfunctions.googleapis.com && gcloud services enable cloudbuild.googleapis.com 
    ```
1. If you want to use firestore 
   1. install [Firebase CLI](https://firebase.google.com/docs/cli)
   1. Edit your project id in `.firebaserc`
   1. enable firestore
   ```bash
   gcloud services enable firestore.googleapis.com && gcloud services enable appengine.googleapis.com
   gcloud app create --region=europe-central2
   gcloud firestore databases create --region=europe-central2
   firebase deploy --only firestore:rules
   ```
1. Deploy (**remember to change entry-point if you are changing group or main class name**)
    ```bash
    gcloud functions deploy hello-function \
   --entry-point=dev.manka.kotlin.function.App \
   --source=build/deploy --runtime=java11 --trigger-http \
   --allow-unauthenticated
   ````

## Testing API
1.Check [postman collection - Firestore.postman_collection.json](/Firestore.postman_collection.json)
1. To create new document use PUT method with JSON
```json
{
    "documentPath":"testCollection/test",
    "content":{
        "name":"Mateusz",
        "country" : "Poland",
        "company" : "manka development"
    }
}
```
1. to read data use GET method with parameter `?documentPath=testCollection/test`
