package uk.ac.tees.mad.S3269326

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.metrics.AwsSdkMetrics.setRegion
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.regions.Regions

object AWSConfig {
    private const val ACCESS_KEY = "AKIASDRANA5WEILEKWZP"
    private const val SECRET_KEY = "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"
    private const val REGION = "eu-north-1"

    val s3Client = AmazonS3Client(
        BasicAWSCredentials(ACCESS_KEY, SECRET_KEY)
    ).apply {
        setRegion(Regions.fromName(REGION))
    }
}
