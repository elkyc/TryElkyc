package com.elkyc.tryelkyc

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import com.elkyc.base.data.auth.WorkplaceConfig
import com.elkyc.core.Elkyc
import com.elkyc.core.elkycConfig
import com.elkyc.core.presentation.confirm.CriteriaModel
import com.elkyc.core.presentation.confirm.confirmStep
import com.elkyc.core.presentation.finish.finishStep
import com.elkyc.core.presentation.guidance.guidanceStep
import com.elkyc.core.presentation.welcome.welcomeStep
import com.elkyc.diia.presentation.diiaStep
import com.elkyc.documentsdk.presentation.doc.DocType
import com.elkyc.documentsdk.presentation.doc.documentCaptureStep
import com.elkyc.facesdk.presentation.matching.matchingStep

class MainActivity : AppCompatActivity() {

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                Log.e("RESULT", it.data?.getStringExtra("RESULT_REQUEST_CODE") ?: "nothing")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        findViewById<Button>(R.id.run).setOnClickListener {

            val confirmCriteria = listOf(
                CriteriaModel(
                    R.drawable.ic_confirm_criteria_5,
                    "The camera doesn’t cut off part of your face and it is clearly seen"
                ),
                CriteriaModel(
                    R.drawable.ic_confirm_criteria_6,
                    "Document data is clearly seen"
                ),
                CriteriaModel(
                    R.drawable.ic_doc_confirm_criteria_3,
                    "There’s no light glare on the document"
                ),
                CriteriaModel(
                    R.drawable.ic_confirm_criteria_7,
                    "The document doesn’t block your face"
                ),
                CriteriaModel(
                    R.drawable.ic_confirm_criteria_8,
                    "Your hand doesn’t block the document data"
                )
            )

            val guidanceList = listOf(
                CriteriaModel(
                    R.drawable.ic_num_1,
                    "Choose a place with a good lighting"
                ),
                CriteriaModel(
                    R.drawable.ic_num_2,
                    "Have your ID documents ready"
                ),
                CriteriaModel(
                    R.drawable.ic_num_3,
                    "Get ready to take photos of your documents and selfies with them"
                ),
                CriteriaModel(
                    R.drawable.ic_num_4,
                    "Accept the policy terms below"
                )
            )

            val steps = welcomeStep {
                title = "Welcome Title"
                text = "Text for the Welcome screen"
                image = R.drawable.img_intro
                btnProceed = "Continue"
                nextStep = guidanceStep {
                    title = "Before you start ☝"
                    guidance = guidanceList
                    policyText = "I have read and accepted the Privacy Policy"
                    policyLinkedText = "Privacy Policy"
                    policyLink = "https://www.iubenda.com/privacy-policy/25050240"
                    btnProceed = "Start your verification"
                    nextStep = diiaStep {
                        title = "You will be forwarded to sign in your Diia application"
                        description = "Make sure you have installed Diia application"
                        image = R.drawable.img_diia_intro
                        btnProceed = "Proceed"
                        titleSuccess = "Your documents are successfully retrieved"
                        imageSuccess = R.drawable.img_diia_success
                        titleFail = "Sorry, we were not able to retrieve your documents"
                        descriptionFail =
                            "We will check the information and a bank specialist will contact you"
                        imageFail = R.drawable.img_diia_fail
                        btnTerminate = "Finish"
                        nextStep = welcomeStep {
                            title = "Face matching"
                            text =
                                "Now take a three-dimensional photo of your face. It is needed to prevent fraud"
                            btnProceed = "Next"
                            image = R.drawable.img_matching_intro
                            nextStep = matchingStep {
                                titleSuccess = "Successful Verification"
                                imageSuccess = R.drawable.img_matching_success
                                btnProceed = "Proceed"
                                titleFail = "We couldn't recognize your face"
                                textFail =
                                    "Make sure there is enough light and nothing is blocking your face: hat, headphones, mask, glasses etc."
                                imageFail = R.drawable.img_matching_fail
                                nextStep = documentCaptureStep {
                                    docType = DocType.SELFIE_WITH_DOCUMENT
                                    title = "Get ready to take a selfie with the document"
                                    instruction = "We need to make sure the document belongs to you"
                                    useGallery = false
                                    btnCapture = "Take a selfie"
                                    image = R.drawable.img_selfie_with_doc_intro
                                    nextStep = confirmStep {
                                        title = "Make sure that:"
                                        criteriaList = confirmCriteria
                                        btnRetry = "Retry"
                                        btnProceed = "Proceed"
                                        nextStep = finishStep {
                                            title = "Thank you!"
                                            text =
                                                "The documents have been sent for verification to our employees. We will send you a notification when everything is ready."
                                            btnProceed = "Finish"
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }


            val config = elkycConfig {
                customFlow = steps
                clientKey = "<PUT CLIENT KEY HERE>"
                appKey = "<PUT APP KEY HERE>"
                workplace = WorkplaceConfig(
                    host = "<PUT WORKPLACE HOST HERE>",
                    authPath = null,
                    mainPath = "/external/sdk/customer-identity",
                    encryptData = false,
                    rssigSalt = null,
                    accessToken = "<PUT TOKEN HERE>",
                    clientSession = "session-id"
                )
            }

            Elkyc.startVerification(
                activity = this,
                config = config,
                requestCode = "RESULT_REQUEST_CODE",
                resultLauncher = resultLauncher,
                printLog = true,
                isDebug = BuildConfig.DEBUG
            )

        }

    }
}