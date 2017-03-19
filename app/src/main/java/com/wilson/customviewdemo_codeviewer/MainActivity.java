package com.wilson.customviewdemo_codeviewer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private CodeViewer codeViewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
        } catch (Exception e) {
            e.printStackTrace();
        }

        codeViewer = (CodeViewer)findViewById(R.id.codeViewer);
        codeViewer.setSourceCode("public class sample extends Object {\n" +
                "  private int integer;\n" +
                "  protected boolean booly;\n" +
                "  private final double tax = 0.05;\n" +
                "  private char c;\n" +
                "  private String s;\n" +
                "\n" +
                "  public sample(int integer, boolean mrBooly)\n" +
                "  {\n" +
                "    this.integer = integer;\n" +
                "    this.booly = mrBooly;\n" +
                "\n" +
                "  }\n" +
                "\n" +
                "  public boolean setOthers(){\n" +
                "    c = ' ';\n" +
                "    s = \"something\\\" something'\"\n" +
                "    char otherchar = '\\\"';\n" +
                "  }\n" +
                "}");
    }
}
