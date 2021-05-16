# GCP Kotlin function

## How to deploy:
1. [install gcloud cli and authenticate]: https://cloud.google.com/sdk/gcloud  
1. build function
   `./gradlew buildFunction`
1. chose your project you can list it with
   `gcloud projects list` 
   Then set it with
   ` gcloud config set project {PROJECT-NAME}`
1. chose region (in example I've used Warsaw)
   `gcloud config set functions/region europe-central2`
1. Enable gcloud services for cloud functions and cloud build
    ` gcloud services enable cloudfunctions.googleapis.com `
    ` gcloud services enable cloudbuild.googleapis.com `
1. Deploy 
    `gcloud functions deploy hello-function \
   --entry-point=xyz.manka.mateusz.App \
   --source=build/deploy --runtime=java11 --trigger-http \
   --allow-unauthenticated
   `