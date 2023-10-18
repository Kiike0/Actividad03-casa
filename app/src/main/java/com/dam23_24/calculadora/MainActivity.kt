package com.dam23_24.calculadora

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.text.DecimalFormat

@Suppress("SpellCheckingInspection")
class MainActivity : AppCompatActivity() {

    private lateinit var txtPantalla: TextView
    private lateinit var txtDetalle: TextView

    private lateinit var btnNum: ArrayList<Button>
    private lateinit var btnOper: ArrayList<Button>

    private lateinit var btnCE: Button
    private lateinit var btnResult: Button

    private lateinit var btnDelete: Button

    private lateinit var calc: Calculo
    private val df = DecimalFormat("#.##")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Creo mi objeto de tipo Calculo.
        calc = Calculo()

        //Inicializamos las variables que corresponderán a cada componente y les asignamos una función al evento que se programa de cada uno.
        initComponents()
        initListeners()
    }

    /**
     * Inicializar las variables que se asignarán a cada componente que vamos a controlar.
     */
    private fun initComponents() {
        txtPantalla = findViewById(R.id.txtPantalla)
        txtDetalle = findViewById(R.id.txtDetalle)

        btnCE = findViewById(R.id.btnCE)
        btnResult = findViewById(R.id.btnResult)

        btnDelete = findViewById(R.id.btnDelete)

        txtPantalla.text = ""
        txtDetalle.text = ""

        initBtnNum()
        initBtnOper()
    }

    /**
     * Establecer las variables del ArraList btnNum que controlarán los dígitos y el punto decimal.
     */
    private fun initBtnNum() {
        btnNum = ArrayList()
        btnNum.add(findViewById(R.id.btn0))
        btnNum.add(findViewById(R.id.btn1))
        btnNum.add(findViewById(R.id.btn2))
        btnNum.add(findViewById(R.id.btn3))
        btnNum.add(findViewById(R.id.btn4))
        btnNum.add(findViewById(R.id.btn5))
        btnNum.add(findViewById(R.id.btn6))
        btnNum.add(findViewById(R.id.btn7))
        btnNum.add(findViewById(R.id.btn8))
        btnNum.add(findViewById(R.id.btn9))
        btnNum.add(findViewById(R.id.btnDec))
    }

    /**
     * Establecer las variables del ArraList btnOper que controlarán los operadores del cálculo.
     */
    private fun initBtnOper() {
        btnOper = ArrayList()
        btnOper.add(findViewById(R.id.btnSuma))
        btnOper.add(findViewById(R.id.btnResta))
        btnOper.add(findViewById(R.id.btnMul))
        btnOper.add(findViewById(R.id.btnDiv))
    }

    /**
     * Establecer los eventos y las funciones asociadas de cada componente que vamos a controlar.
     */
    private fun initListeners() {
        for (i in 0..<btnNum.count()) {
            btnNum[i].setOnClickListener { btnNumClicked(i) }
        }

        for (i in 0..<btnOper.count()) {
            btnOper[i].setOnClickListener { btnOperClicked(i) }
        }

        btnCE.setOnClickListener { btnCEClicked() }
        btnResult.setOnClickListener { btnResultClicked() }
        btnDelete.setOnClickListener { btnDeleteClicked() }
    }

    /**
     * Agrega el dígito pulsado en el número correspondiente del objeto calc.
     *
     * @param num dígito pulsado del 0 al 9 o punto decimal (10)
     */
    private fun btnNumClicked(num: Int) {
        calc.tecleaDigito(num)

        //Mostramos info actualizada en los TextView de la app
        if (calc.primerNum) {
            muestraValor(calc.numTemp1, calc.numTemp1)
        } else {
            muestraValor(calc.numTemp2, calc.numTemp1 + calc.operadorTxt() + calc.numTemp2)
        }
    }

    /**
     * Agrega la operación del cálculo a realizar
     *
     * @param num operación (0 -> + / 1 -> - / 2 -> * / 3 -> /
     */
    private fun btnOperClicked(num: Int) {
        if (calc.primerNum) {
            //Tratamiento de la operación cuando estamos introduciendo el primer número.

            if (calc.numCalculos > 0 && calc.numTemp1 == "") {
                //Si hay un cálculo anterior y el num1 aún está vacío, el resultado anterior es el num1 del siguiente cálculo.
                calc.num1 = calc.result
                calc.numTemp1 = df.format(calc.result).toString()
            } else {
                //Sino, asignamos num1 del objeto calc convirtiendo los dígitos introducidos a float.
                //Además, si existe algún problema o cuando si se pulsa un operador sin introducir número antes, lo capturamos y usamos el valor 0.
                try {
                    calc.num1 = calc.numTemp1.toFloat()
                } catch (e: NumberFormatException) {
                    calc.num1 = 0f
                    calc.numTemp1 = "0"
                }
            }

            //Asignamos el operador al objeto calc, mostramos info en pantalla y actualizamos las características necesarias de calc para indicar que pasamos al estado de introducir el segundo número.
            calc.op = num
            muestraValor(calc.operadorTxt(), calc.numTemp1 + calc.operadorTxt())
            calc.numTemp2 = ""
            calc.primerNum = false
        } else if (calc.numTemp2 == "") {
            //Si se introduce una operación y aún no existe el segundo número la nueva operación debe reemplazar la operación anterior.

            calc.op = num
            //Mostramos en pantalla la actualización del operador.
            muestraValor(calc.operadorTxt(), calc.numTemp1 + calc.operadorTxt())
        } else {
            //Tratamiento de la operación cuando estamos introduciendo el segundo número.

            //Convertimos la cadena de dígitos en el número 2 y realizamos el cálculo.
            //Si existe algún problema en la conversión la controlamos asignando el valor 0.
            calc.num2 = try {
                calc.numTemp2.toFloat()
            } catch (e: NumberFormatException) {
                0f
            }
            calc.calcular()

            //Mostramos en pantalla el resultado del cálculo como detalle y la operación en la pantalla principal.
            muestraValor(
                calc.operadorTxt(num),
                df.format(calc.result).toString() + calc.operadorTxt(num)
            )

            //Actualizamos las características necesarias del objeto calc, ya que vamos a seguir en el estado de introducir solo un segundo número, ya que el primer número y la operación es asignado como el resultado del cálculo realizado y la nueva operación introducida.
            calc.num1 = calc.result
            calc.op = num
            calc.num2 = 0f
            calc.numTemp1 = df.format(calc.num1).toString()
            calc.numTemp2 = ""
        }
        calc.boolOperacion = true //Señalamos que ya hay una operación en curso
    }

    /**
     * Reiniciar las características del objeto calc cuando se pulsa el botón CE
     */
    private fun btnCEClicked() {
        //Mostramos en pantalla y detalle la cadena de caracteres vacía.
        muestraValor("", "")

        //Inicializamos las características del objeto calc.
        calc.iniValores()

        //Los boolean también los reiniciamos
        calc.boolResultado = false
        calc.boolOperacion = false
    }

    /**
     * Acciones a realizar al pulsar el botón =.
     * Solo se ejecutará si estamos introduciendo el segundo número.
     * Realizará las acciones necesarias para mostrar el cálculo en pantalla.
     */
    private fun btnResultClicked() {
        if (!calc.primerNum && calc.numTemp2 != "") {
            //Si estamos introduciendo el segundo número, lo actualizamos convirtiendo la cadena de dígitos y calculamos la operación.
            calc.num2 = try {
                calc.numTemp2.toFloat()
            } catch (e: NumberFormatException) {
                0f
            }
            calc.calcular()

            //Mostramos en pantalla el resultado y en detalle toda la operación (num1 + num2 = result) formateando a 2 posiciones decimales.
            muestraValor(
                df.format(calc.result).toString(),
                df.format(calc.num1).toString() + calc.operadorTxt() + df.format(calc.num2)
                    .toString() + "=" + df.format(calc.result).toString()
            )

            //Inicializamos las características del objeto calc, excepto el núemro de cálculos.
            calc.iniValores(resetNumCalculos = false, resetResult = false)
            calc.boolResultado =
                true //Se introduce en el programa para mejorar el funcionamiento de DELETE
        } else {
            mensajeError("Debe introducir 2 números y una operación para mostrar un resultado")
        }
    }

    /**
     * Acciones a realizar al pulsar el botón <.
     * Solo se ejecutará si hay número en pantalla si no, dará un mensaje de error.
     * Borra el último número pulsado y el último operador pulsado, si se llega a el después
     * de borrar num2
     * Además dará un mensaje de error si hay un resultado en pantalla, ya que no queremos borrar
     * el resultado
     */
    private fun btnDeleteClicked() {
        if (calc.numTemp1.isEmpty()) {
            mensajeError("No existe nada para borrar") //Si no hay nada para borrar salta este error
            //Además inicializamos las características del objeto calc. para solucionar posibles errores
            calc.iniValores()

        } else if (calc.numTemp1.isNotEmpty() && !calc.boolOperacion && !calc.boolResultado) {
            calc.numTemp1 =
                calc.numTemp1.dropLast(1) //Eliminamos el último número llamando a dropLast
            muestraValor(
                calc.numTemp1,
                calc.numTemp1
            ) //Lo guardamos en muestraValor una vez hecha la modificación

            //Seguimos borrando hasta borrarlo todo, borramos el operador
        } else if (calc.numTemp2.isEmpty() && !calc.boolResultado) {
            calc.numCalculos = 0 //Reiniciamos el valor de numCalculos
            calc.op = 0 //Hacemos lo mismo con el valor de op
            muestraValor("", calc.numTemp1) //borramos el operador
            calc.boolOperacion = false //indicamos que ya no hay operador

        } else if (calc.numTemp2.isNotEmpty() && !calc.boolResultado) {
            //Hacemos lo mismo de num1 pero con num2
            calc.numTemp2 = calc.numTemp2.dropLast(1)
            muestraValor(calc.numTemp2, calc.numTemp1 + calc.operadorTxt(calc.op) + calc.numTemp2)

        } else {
            //Si se ha pulsado el igual sale el mensaje de Error llamando al método donde está el Toast
            mensajeError("No existe nada para borrar")
        }
    }


    /**
     * Muestra la información en los componentest TextView txtPantalla y txtDetalle.
     *
     * @param pantalla info a mostrar en txtPantalla
     * @param detalle info a mostrar en txtDetalle
     */
    private fun muestraValor(pantalla: String, detalle: String) {
        txtPantalla.text = getString(R.string.txt_txtPantalla, pantalla)
        txtDetalle.text = getString(R.string.txt_txtDetalle, detalle)
    }


    /**
     * Muestra un mensaje de error en pantalla durante un tiempo corto.
     *
     * @msj mensaje de error
     */
    private fun mensajeError(msj: String) {
        Toast.makeText(this, msj, Toast.LENGTH_SHORT).show()
    }
}