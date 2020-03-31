package com.example.qrcodetest;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class GenerateCode extends AppCompatActivity {

    private EditText genEditText;
    private Button genCodeBtn;
    private ImageView qrCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_code);

        genEditText = findViewById(R.id.genEditText);
        genCodeBtn = findViewById(R.id.genCodeBtn);
        qrCode = findViewById(R.id.qrCode);

        genCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                int width = 200;
                int height = 200;
                String textGen = genEditText.getText().toString();

                //Makes phone keyboard disappear when button is clicked
                genEditText.onEditorAction(EditorInfo.IME_ACTION_DONE);

                //Creates and displays QR code
                try {
                    BitMatrix bitMatrix = qrCodeWriter.encode(textGen, BarcodeFormat.QR_CODE, width, height);
                    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                        }
                    }

                    qrCode.setImageBitmap(bitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Keeps 'generate code' button disabled until user enters text
                genEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String fieldContent = genEditText.getText().toString().trim();

                        genCodeBtn.setEnabled(!fieldContent.isEmpty());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

            }
        });
    }
}
