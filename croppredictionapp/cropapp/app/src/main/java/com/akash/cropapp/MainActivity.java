package com.akash.cropapp;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.tensorflow.lite.Interpreter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {

    private EditText editTextN, editTextP, editTextK, editTextTemperature, editTextHumidity,
            editTextPh, editTextRainfall;
    private Button buttonPredict, buttonReset;
    private TextView textViewResult;

    private Interpreter interpreter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            interpreter = new Interpreter(loadModelFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        editTextN = findViewById(R.id.editTextN);
        editTextP = findViewById(R.id.editTextP);
        editTextK = findViewById(R.id.editTextK);
        editTextTemperature = findViewById(R.id.editTextTemperature);
        editTextHumidity = findViewById(R.id.editTextHumidity);
        editTextPh = findViewById(R.id.editTextPh);
        editTextRainfall = findViewById(R.id.editTextRainfall);

        buttonPredict = findViewById(R.id.buttonPredict);
        buttonReset = findViewById(R.id.buttonReset);
        textViewResult = findViewById(R.id.textViewResult);

        buttonPredict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePrediction();
            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFields();
            }
        });
    }

    private ByteBuffer loadModelFile() throws IOException {
        // Load the model file from assets
        ByteBuffer modelBuffer;
        Context context = getApplicationContext();
        try (InputStream inputStream = context.getAssets().open("crop_model.tflite")) {
            int size = inputStream.available();
            modelBuffer = ByteBuffer.allocateDirect(size);
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            modelBuffer.put(buffer);
        }
        return modelBuffer;
    }

    private void makePrediction() {
        try {
            // Get input values
            float[] inputValues = getInputValues();

            // Run model inference
            float[][] outputValues = new float[1][22];  // Replace 22 with the actual number of classes in your model

            interpreter.run(inputValues, outputValues);

            // Display the prediction
            int predictedClass = getPredictedClass(outputValues[0]);
            String resultText = "Predicted Crop: " + getClassName(predictedClass);
            textViewResult.setText(resultText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private float[] getInputValues() {
        // Get input values from EditText fields and convert to float array
        float[] inputValues = new float[7];
        inputValues[0] = Float.parseFloat(editTextN.getText().toString());
        inputValues[1] = Float.parseFloat(editTextP.getText().toString());
        inputValues[2] = Float.parseFloat(editTextK.getText().toString());
        inputValues[3] = Float.parseFloat(editTextTemperature.getText().toString());
        inputValues[4] = Float.parseFloat(editTextHumidity.getText().toString());
        inputValues[5] = Float.parseFloat(editTextPh.getText().toString());
        inputValues[6] = Float.parseFloat(editTextRainfall.getText().toString());
        return inputValues;
    }

    private int getPredictedClass(float[] outputValues) {
        // Find the index of the maximum value in the output array
        int maxIndex = 0;
        for (int i = 1; i < outputValues.length; i++) {
            if (outputValues[i] > outputValues[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    private String getClassName(int classIndex) {
        // Map class index to class name based on your label list
        String[] classNames = {"apple", "banana", "blackgram", "chickpea", "coconut", "coffee", "cotton", "grapes", "jute", "kidneybeans", "lentil", "maize", "mango", "mothbeans", "mungbean", "muskmelon", "orange", "papaya", "pigeonpeas", "pomegranate", "rice", "watermelon"};
        return classNames[classIndex];
    }


    private void resetFields() {
        // Clear the values in all EditText fields
        editTextN.setText("");
        editTextP.setText("");
        editTextK.setText("");
        editTextTemperature.setText("");
        editTextHumidity.setText("");
        editTextPh.setText("");
        editTextRainfall.setText("");
        textViewResult.setText("");
    }
}
