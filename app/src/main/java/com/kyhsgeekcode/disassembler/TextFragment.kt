package com.kyhsgeekcode.disassembler

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.CharacterStyle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.kyhsgeekcode.disassembler.FileTabFactory.TextFileTabFactory
import com.kyhsgeekcode.disassembler.project.ProjectDataStorage
import kotlinx.android.synthetic.main.fragment_text.*
import kotlinx.serialization.UnstableDefault
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStreamReader
import java.util.*

class TextFragment : Fragment() {
    val ARG_PARAM = "param"
    private lateinit var fileContent: ByteArray
    private lateinit var relPath: String
    @UnstableDefault
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            relPath = it.getString(ARG_PARAM)!!
        }
        Log.d(TAG, "relPath:$relPath")
        fileContent = ProjectDataStorage.getFileContent(relPath)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.fragment_text, container, false)!!

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val ssb = SpannableStringBuilder()
        val terms: List<String>? = TermList[File(relPath).extension.toLowerCase()]

        val br = BufferedReader(InputStreamReader(ByteArrayInputStream(fileContent)))
        var line: String?
        while (br.readLine().also { line = it } != null) { //https://stackoverflow.com/a/46390973/8614565
            val ss = SpannableString(line)
            if (terms != null) {
                for (term in terms) {
                    Log.v("TextFactory", "Checking:$term")
                    var ofe = line!!.indexOf(term)
                    Log.v("TextFactory", "ofe:$ofe")
                    var ofs = 0
                    while (ofs < line!!.length && ofe != -1) {
                        ofe = line!!.indexOf(term, ofs)
                        if (ofe == -1) break else {
                            ss.setSpan(CharacterStyle.wrap(TextFileTabFactory.spanBlue), ofe, ofe + term.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                        ofs = ofe + 1
                    }
                }
            }
            ssb.append(ss)
            ssb.append(System.lineSeparator())
            Log.d(TAG, "ss:$ss")
        }
        Log.d(TAG, "ss:$ssb")
        textFragmentTextView.setText(ssb, TextView.BufferType.SPANNABLE)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param fileContent Parameter 1.
         * @return A new instance of fragment HexFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(relPath: String) =
                TextFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM, relPath)
                    }
                }

        val TermList: MutableMap<String, List<String>> = HashMap()
        fun loadTerms() {
            val smaliterms: MutableList<String> = ArrayList()
            smaliterms.add(".class")
            smaliterms.add(".super")
            smaliterms.add(".source")
            smaliterms.add(".implements")
            smaliterms.add(".field")
            smaliterms.add(".method")
            TermList["smali"] = smaliterms
            TermList["il"] = smaliterms
        }

        init {
            loadTerms()
        }
    }
}