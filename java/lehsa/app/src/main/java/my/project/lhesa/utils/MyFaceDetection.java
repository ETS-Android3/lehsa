package my.project.lhesa.utils;

/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.util.List;

import my.project.lhesa.FacultyFaceDetect;


public class MyFaceDetection {
    private static final String TAG = FacultyFaceDetect.class.getSimpleName();

    // [START set_detector_options]
    private final FaceDetectorOptions options = new FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .build();
    // [END set_detector_options]

    public Task<List<Face>> detectFaces(InputImage image) {
        // [START get_detector]
        FaceDetector detector = FaceDetection.getClient(options);
        // Or use the default options:
        // FaceDetector detector = FaceDetection.getClient();
        // [END get_detector]

        // [START run_detector]
        Task<List<Face>> result = detector.process(image)
                .addOnSuccessListener(
                    new OnSuccessListener<List<Face>>() {
                        @Override
                        public void onSuccess(List<Face> faces) {
                            if (!faces.isEmpty()) {
                                Log.i(TAG, "No faces detected");
                            }
                        }
                    })
                .addOnFailureListener(
                    new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Task failed with an exception
                            Log.e(TAG, e.getMessage());
                        }
                    });
            // [END run_detector]
        return result;
    }
}