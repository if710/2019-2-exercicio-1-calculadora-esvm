package br.ufpe.cin.android.calculadora

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setButtonListeners()
    }

    // seta o listener de todos os botões concatenando o texto do botão no que já tem na caixa de texto
    // para os casos de números e operadores.
    // no caso do clear, limpa o que tem na caixa de texto
    // no caso do equal, calcula o valor e seta em text_info
    private fun setButtonListeners() {
        btn_0.setOnClickListener{setText(btn_0.text)}
        btn_1.setOnClickListener{setText(btn_1.text)}
        btn_2.setOnClickListener{setText(btn_2.text)}
        btn_3.setOnClickListener{setText(btn_3.text)}
        btn_4.setOnClickListener{setText(btn_4.text)}
        btn_5.setOnClickListener{setText(btn_5.text)}
        btn_6.setOnClickListener{setText(btn_6.text)}
        btn_7.setOnClickListener{setText(btn_7.text)}
        btn_8.setOnClickListener{setText(btn_8.text)}
        btn_9.setOnClickListener{setText(btn_9.text)}
        btn_Dot.setOnClickListener{setText(btn_Dot.text)}
        btn_LParen.setOnClickListener{setText(btn_LParen.text)}
        btn_RParen.setOnClickListener{setText(btn_RParen.text)}
        btn_Power.setOnClickListener{setText(btn_Power.text)}
        btn_Add.setOnClickListener{setText(btn_Add.text)}
        btn_Subtract.setOnClickListener{setText(btn_Subtract.text)}
        btn_Divide.setOnClickListener{setText(btn_Divide.text)}
        btn_Multiply.setOnClickListener{setText(btn_Multiply.text)}
        btn_Equal.setOnClickListener{text_info.text = eval(text_calc.text.toString()).toString()}
        btn_Clear.setOnClickListener{text_calc.setText("")}
    }

    // concatena o texto do botão com o que já tem na caixa de texto
    private fun setText(text: CharSequence?) {
        text_calc.text = Editable.Factory.getInstance().newEditable(text_calc.text.toString() + text)
    }

    //Como usar a função:
    // eval("2+2") == 4.0
    // eval("2+3*4") = 14.0
    // eval("(2+3)*4") = 20.0
    //Fonte: https://stackoverflow.com/a/26227947
    fun eval(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch: Char = ' '
            fun nextChar() {
                val size = str.length
                ch = if ((++pos < size)) str.get(pos) else (-1).toChar()
            }

            fun eat(charToEat: Char): Boolean {
                while (ch == ' ') nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                // exibe o toast ao invés de estourar a exception. $ch é um template e indica que o valor da variável
                // ch será inserida nesse local da string
                if (pos < str.length) showToast("Caractere inesperado: $ch")
                return x
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            // | number | functionName factor | factor `^` factor
            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'))
                        x += parseTerm() // adição
                    else if (eat('-'))
                        x -= parseTerm() // subtração
                    else
                        return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'))
                        x *= parseFactor() // multiplicação
                    else if (eat('/'))
                        x /= parseFactor() // divisão
                    else
                        return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+')) return parseFactor() // + unário
                if (eat('-')) return -parseFactor() // - unário
                var x: Double
                val startPos = this.pos
                if (eat('(')) { // parênteses
                    x = parseExpression()
                    eat(')')
                } else if ((ch in '0'..'9') || ch == '.') { // números
                    while ((ch in '0'..'9') || ch == '.') nextChar()
                    x = java.lang.Double.parseDouble(str.substring(startPos, this.pos))
                } else if (ch in 'a'..'z') { // funções
                    while (ch in 'a'..'z') nextChar()
                    val func = str.substring(startPos, this.pos)
                    x = parseFactor()
                    if (func == "sqrt")
                        x = Math.sqrt(x)
                    else if (func == "sin")
                        x = Math.sin(Math.toRadians(x))
                    else if (func == "cos")
                        x = Math.cos(Math.toRadians(x))
                    else if (func == "tan")
                        x = Math.tan(Math.toRadians(x))
                    else
                        showToast("Função desconhecida: $func")
                } else {
                    // se der erro, exibe o toast e retorna zero
                    showToast("Caractere inesperado: $ch")
                    return 0.0
                }
                if (eat('^')) x = Math.pow(x, parseFactor()) // potência
                return x
            }
        }.parse()
    }

    // exibe o toast com o erro na variável text. O Toast.LENGTH_LONG indica por quanto tempo
    // o toast vai permanecer na tela
    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}
