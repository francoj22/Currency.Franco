package ie.gutierre.franco.currencyfranco;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @version 1.0
 * @Description Simple android application that will do
 * conversion of multiple currencies. Will convert between the following six
 * currencies: {US Dollars, Euros, British Pounds, Canadian Dollars, Australian Dollars, Japanese Yen}.
 * The conversion rates are the following.
 * 1 Euro = 0.7289 Pounds
 * 1 Euro = 1.12974 US Dollars
 * 1 Euro = 136.156 Japanese Yen
 * 1 Euro = 1.57155 Austrailian Dollars
 * 1 Euro = 1.49167 Canadian Dollars
 * @autor Franco Gutierrez
 * @since 27-10-2015
 */

public class MainActivity extends AppCompatActivity {
    private Spinner spinner;            //will show a list of currencys that can be evaluated
    private Button btn_convert;         //it will content the action button keep update the listView
    private EditText et_converterValue; //keep the value to be evaluate
    private TextView spinnerText;        //auxiliar value using to detected the spinner value selected
    private ListView lv_mainlist;       //listView that will kee
    private ArrayList<String> al_strings;  //List of values that has been converted in the Adapter
    private ArrayAdapter<String> aa_strings;// ArrayAdapter that will update the ListView
    String[] keys, valuesString; //get all the values from the converter for the ht variable
    Map<String, Double> ht = new LinkedHashMap<String, Double>(); //Map that will keep all the information from the currency string to the value
    String[] spinnerArray = new String[4]; //array that will keep update the spinner variable
    boolean firstCall = true;

    /**
     *
     */
    public void init() {
        al_strings = new ArrayList<String>();
        ht = new LinkedHashMap<String, Double>();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            setContentView(R.layout.activity_main);
            init();
            //Defining the list of converter options
            spinner = (Spinner) findViewById(R.id.spinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    this,
                    R.array.spinner_converter,
                    android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //item that was selected
                    spinnerText.setText(parent.getItemAtPosition(position).toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    spinnerText.setText("EUR");
                }
            });

            //setting an editor action to respond to input events
            et_converterValue = (EditText) findViewById(R.id.et_converterValue);
            spinnerText = (TextView) findViewById(R.id.tv_display);
            et_converterValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    //if it has been clicked then will udpdate the results
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        try {
                            convert_value(ht, keys, al_strings, Double.parseDouble(et_converterValue.getText().toString()), spinnerText.getText().toString(), firstCall);
                            aa_strings.notifyDataSetChanged();
                        } catch (Exception exception) {
                            System.out.println(exception.getMessage());

                        }
                        return true;
                    }
                    // if this event is not handle return
                    return false;
                }
            });

            btn_convert = (Button) findViewById(R.id.btn_convert);
            btn_convert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        convert_value(ht, keys, al_strings, Double.parseDouble(et_converterValue.getText().toString()), spinnerText.getText().toString(), firstCall);
                        aa_strings.notifyDataSetChanged();
                    } catch (Exception exception) {
                        System.out.println(exception.getMessage());

                    }

                }
            });
            lv_mainlist = (ListView) findViewById(R.id.lv_mainlist);
            spinnerArray = getResources().getStringArray(R.array.spinner_converter_textView).clone();
            String[] valuesString = getResources().getStringArray(R.array.spinner_converter_values).clone();
            String[] spinner = getResources().getStringArray(R.array.spinner_converter).clone();
            for (int i = 0; i < spinner.length; i++) {
                ht.put(spinner[i], Double.parseDouble(valuesString[i]));
            }


            if (et_converterValue.getText().toString().equals(""))
                et_converterValue.setText("1");
            convert_value(ht, getResources().getStringArray(R.array.spinner_converter), al_strings, Double.parseDouble(et_converterValue.getText().toString()), spinnerText.getText().toString(), firstCall);
            firstCall = false;
            //create an array adapter for the variable al_strings and set it in the  lv_mainlist
            aa_strings = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, al_strings);
            lv_mainlist.setAdapter(aa_strings);
        } catch (Exception e) {
            System.out.println("");
        }
    }

    /**
     * This function will keep the intelligence of the currency converter app
     * it works like the following pseudocode
     * if(value='EUR')
     * it will calculate 1/currencyValueSelected
     * else
     * if (value=='currencyValueSelected')
     * Does not calculate anything
     * else
     * Calculate (value * valueAux.getValue() (different to the current selected)) / NewValue(currencyValueGiven);
     *
     * @param ht
     * @param keys
     * @param al_strings
     * @param v
     * @param vName
     * @param firstCall
     */


    private void convert_value(Map<String, Double> ht, String[] keys, ArrayList<String> al_strings, Double v, String vName, boolean firstCall) {
        try {

            String result = "";
            int counterSpinner = 0;
            int counterListviewItems = 0;
            boolean firstCallItem = true;
            Double resultEur = 1.0;
            DecimalFormat df = new DecimalFormat("#.####");
            if (vName.equals("EUR")) {
                for (Map.Entry<String, Double> value : ht.entrySet()) {

                    if (firstCall) {
                        al_strings.add(String.valueOf(v * value.getValue()) + " " + value.getKey());
                    } else
                        al_strings.set(counterListviewItems, String.valueOf(v * value.getValue()) + " " + value.getKey());
                    counterListviewItems++;
                }
            } else {
                Double NewValue = 1.0;

                for (Map.Entry<String, Double> valueAux : ht.entrySet()) {
                    if (valueAux.getKey().equals("EUR")) {
                        if (firstCallItem) {
                            for (Map.Entry<String, Double> value : ht.entrySet()) {
                                if (value.getKey().equals(vName)) {

                                    resultEur = (1.0 / value.getValue()) * v;
                                    NewValue = value.getValue();
                                }

                            }
                            firstCallItem = false;
                            al_strings.set(counterListviewItems, String.valueOf(df.format(resultEur)) + " " + spinnerArray[counterSpinner]);

                        }
                    } else {

                        if (valueAux.getKey().equals(vName)) {
                            al_strings.set(counterListviewItems, String.valueOf(df.format(v)) + " " + spinnerArray[counterSpinner]);
                        }

                        if (!(valueAux.getKey().equals(vName))) {


                            resultEur = (v * valueAux.getValue()) / NewValue;
                            al_strings.set(counterListviewItems, String.valueOf(df.format(resultEur)) + " " + spinnerArray[counterSpinner]);
                        }

                    }
                    ++counterSpinner;
                    ++counterListviewItems;
                }


            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
