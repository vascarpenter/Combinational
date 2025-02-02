import java.awt.Font
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.datatransfer.UnsupportedFlavorException
import java.io.IOException
import java.util.*
import javax.swing.JFrame
import javax.swing.UIManager

/*
  This program converts clipboard:
    # header comment
    # header comment continues until # + number comment lines
    # 1 .. series 1
    some prompt
    # 2 .. series 2
    another prompt
    # 3 .. series 3
    just another prompt

  to  2, 1, 3 order
    # header comment
    # header comment continues until # + number comment lines
    # 2 .. series 2
    another prompt
    # 1 .. series 1
    some prompt
    # 3 .. series 3
    just another prompt
 */

fun main()
{
    val f = JFrame("v1.1 SD prompts Combinational Reorderer")
    val g = gui()
    UIManager.put("OptionPane.messageFont", Font("Dialog", Font.PLAIN, 12))
    UIManager.put("OptionPane.buttonFont", Font("Dialog", Font.PLAIN, 12))
    f.contentPane = g.panel
    f.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    f.setSize(900, 600)
    f.isResizable = false // 20250403
    f.setLocationRelativeTo(null)
    f.isVisible = true
    g.convertButton.addActionListener {
        convertText(g)
    }
    g.resetButton.addActionListener {
        resetText(g)
    }
}

fun notPrompt(g: gui)
{
    g.textArea1.text = "StableDiffusion promptではないようです"
}

fun isNumeric(toCheck: String): Boolean
{
    return toCheck.toIntOrNull() != null
}

fun analyzeText(txx: String) : Map<Int, String>
{
    val prompts = mutableMapOf<Int,String>()

    var tx = txx
    if (!tx.endsWith("\n"))
    {
        // 改行で終わっていない場合追加しておく
        tx += "\n"
    }

    var headerblock = ""
    val st = StringTokenizer(tx, "\n")
    if (!st.hasMoreTokens())
    {
        return prompts
    }
    var t = st.nextToken().trim { it <= ' ' } // １行

    // read 1 line
    while (st.hasMoreTokens())
    {
        if (t.startsWith("# "))
        {
            // comment header block
            while (st.hasMoreTokens())
            {
                if (t.length >= 3)
                {
                    val ch = t.substring(2, 3)
                    if (isNumeric(ch)) break
                    headerblock += t + "\n"
                }
                t = st.nextToken().trim { it <= ' ' }
            }
            val ch = t.substring(2, 3)
            if (isNumeric(ch))
            {
                val regex = Regex("""\d+""")
                val match = regex.find(t.substring(2))
                if (match != null)
                {
                    val rownum = match.value.toIntOrNull() ?: 0
                    var prompt = ""

                    while (st.hasMoreTokens())
                    {
                        prompt += t + "\n"

                        t = st.nextToken().trim { it <= ' ' }
                        if (t.startsWith("# ")) break
                        if (!st.hasMoreTokens())
                        {
                            // if end of document, add last line to prompt
                            prompt += t + "\n"
                            break
                        }
                    }
                    prompts.putIfAbsent(rownum, prompt)   // put at rownum , override if it exists, add if absent
                }
            }
        }
        else
        {
            t = st.nextToken().trim { it <= ' ' }
        }
    }

    prompts.putIfAbsent(-1,headerblock)
    return prompts
}

fun resetText(g: gui)
{
    val tx = getClipboardString()
    val prompts = analyzeText(tx)
    if (prompts.size < 2)
    {
        notPrompt(g)
        return
    }

    var tt = prompts.getOrDefault(-1, "")
    val maxsize = prompts.size - 1   // prompts.size = max num of prompts + headerblock, so -1
    for (i in 1..maxsize)
    {
        tt += prompts.getOrDefault(i,"")        // if num absent, skip with ""
    }
    g.textArea1.text = tt
    setClipboardString(tt)

}

fun convertText(g: gui)
{
    // add more lines... made by copilot
    val sequence = arrayOf(
        arrayOf(2, 1),
        arrayOf(2, 1, 3),
        arrayOf(2, 1, 3, 4),
        arrayOf(2, 1, 5, 3, 4),
        arrayOf(2, 1, 5, 6, 3, 4),
        arrayOf(2, 7, 1, 5, 6, 3, 4),
        arrayOf(2, 7, 1, 5, 6, 3, 4, 8),
        arrayOf(2, 9, 7, 1, 5, 6, 3, 4, 8),
        arrayOf(2, 9, 7, 1, 5, 6, 3, 10, 4, 8),
        arrayOf(2, 9, 7, 1, 5, 6, 3, 10, 4, 8, 11),
        arrayOf(2, 9, 12, 7, 1, 5, 6, 3, 10, 4, 8, 11),
        arrayOf(2, 9, 12, 7, 1, 5, 6, 3, 10, 13, 4, 8, 11),
        arrayOf(2, 9, 12, 7, 1, 5, 6, 3, 10, 13, 4, 8, 11, 14),
        arrayOf(2, 9, 12, 7, 1, 5, 6, 3, 15, 10, 13, 4, 8, 11, 14),
        arrayOf(2, 9, 12, 7, 1, 5, 6, 3, 15, 10, 13, 4, 16, 8, 11, 14),
        arrayOf(2, 9, 12, 7, 1, 5, 6, 3, 15, 10, 13, 4, 16, 17, 8, 11, 14),
        arrayOf(2, 9, 12, 7, 1, 5, 18, 6, 3, 15, 10, 13, 4, 16, 17, 8, 11, 14),
        arrayOf(19, 2, 9, 12, 7, 1, 5, 18, 6, 3, 15, 10, 13, 4, 16, 17, 8, 11, 14),
        arrayOf(19, 2, 9, 12, 7, 1, 5, 18, 6, 3, 20, 15, 10, 13, 4, 16, 17, 8, 11, 14),
        arrayOf(19, 2, 9, 12, 7, 21, 1, 5, 18, 6, 3, 20, 15, 10, 13, 4, 16, 17, 8, 11, 14),
        arrayOf(19, 2, 9, 12, 7, 21, 1, 5, 18, 6, 22, 3, 20, 15, 10, 13, 4, 16, 17, 8, 11, 14),
        arrayOf(19, 2, 9, 12, 7, 21, 1, 5, 18, 6, 22, 3, 23, 20, 15, 10, 13, 4, 16, 17, 8, 11, 14),
        arrayOf(19, 2, 9, 12, 7, 21, 1, 5, 18, 6, 24, 22, 3, 23, 20, 15, 10, 13, 4, 16, 17, 8, 11, 14),
        arrayOf(19, 2, 9, 12, 25, 7, 21, 1, 5, 18, 6, 24, 22, 3, 23, 20, 15, 10, 13, 4, 16, 17, 8, 11, 14),
        arrayOf(19, 2, 9, 12, 25, 7, 21, 1, 5, 18, 6, 24, 22, 3, 23, 20, 15, 10, 13, 4, 16, 17, 8, 11, 26, 14),
        arrayOf(19, 2, 9, 12, 25, 7, 21, 1, 5, 18, 6, 24, 22, 3, 23, 20, 15, 10, 13, 4, 16, 27, 17, 8, 11, 26, 14),
        arrayOf(19, 2, 9, 12, 25, 7, 21, 1, 5, 18, 6, 24, 22, 3, 23, 20, 15, 10, 13, 4, 16, 27, 17, 8, 11, 26, 27, 14),
        arrayOf(19, 2, 9, 12, 25, 7, 21, 1, 5, 18, 6, 24, 22, 3, 23, 20, 15, 10, 13, 4, 16, 27, 17, 29, 8, 11, 26, 28, 14),
        arrayOf(19, 2, 9, 12, 25, 7, 21, 1, 5, 18, 6, 24, 22, 3, 23, 20, 15, 10, 13, 4, 16, 27, 17, 29, 8, 11, 26, 28, 14, 30),
        arrayOf(19, 2, 9, 12, 25, 7, 21, 1, 5, 18, 6, 24, 22, 3, 23, 20, 15, 10, 31, 13, 4, 16, 27, 17, 29, 8, 11, 26, 28, 14, 30),
        arrayOf(19, 2, 9, 12, 25, 7, 21, 1, 5, 18, 6, 24, 22, 3, 23, 20, 15, 10, 31, 13, 32, 4, 16, 27, 17, 29, 8, 11, 26, 28, 14, 30),
        arrayOf(19, 2, 9, 12, 25, 7, 21, 1, 5, 18, 6, 24, 22, 3, 23, 20, 15, 10, 31, 13, 32, 4, 16, 27, 17, 33, 29, 8, 11, 26, 28, 14, 30),
        arrayOf(19, 2, 9, 12, 25, 7, 21, 1, 5, 18, 6, 24, 22, 3, 23, 20, 15, 10, 31, 13, 32, 4, 16, 27, 17, 33, 34, 29, 8, 11, 26, 28, 14, 30),
        arrayOf(19, 2, 35, 9, 12, 25, 7, 21, 1, 5, 18, 6, 24, 22, 3, 23, 20, 15, 10, 31, 13, 32, 4, 16, 27, 17, 33, 34, 29, 8, 11, 26, 28, 14, 30)
    )


    val tx = getClipboardString()
    val prompts = analyzeText(tx)
    if (prompts.size < 2)
    {
        notPrompt(g)
        return
    }

    // 2 1 5 3 4 .. should 2 1 4 5 3
    var tt = prompts.getOrDefault(-1, "")
    val maxsize = prompts.size - 1   // prompts.size = max num of prompts + headerblock, so -1
    var seq = IntArray(maxsize)
    for (i in 0..<maxsize)
    {
        seq[i] = sequence[maxsize - 2][i]  // why 2?  array starts 2
    }
//    println( seq.contentToString())

    for (i in 0..<maxsize)
    {
        tt += prompts.getOrDefault(seq[i], "")
    }
    g.textArea1.text = tt
    setClipboardString(tt)
}

fun setClipboardString(text: String)
{
    val kit = Toolkit.getDefaultToolkit()
    val clip = kit.systemClipboard
    val ss = StringSelection(text)
    clip.setContents(ss, ss)
}

fun getClipboardString(): String
{
    val kit = Toolkit.getDefaultToolkit()
    val clip = kit.systemClipboard
    val contents = clip.getContents(null)
    var result = ""
    val hasTransferableText = (contents != null
            && contents.isDataFlavorSupported(DataFlavor.stringFlavor))
    if (hasTransferableText)
    {
        try
        {
            result = contents
                .getTransferData(DataFlavor.stringFlavor) as String
        }
        catch (ex: UnsupportedFlavorException)
        {
            // highly unlikely since we are using a standard DataFlavor
            println(ex)
            ex.printStackTrace()
        }
        catch (ex: IOException)
        {
            println(ex)
            ex.printStackTrace()
        }
    }
    return result
}