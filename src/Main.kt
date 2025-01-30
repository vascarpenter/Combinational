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

fun analyzeText(t: String) : Map<Int, String>
{
    var prompts = mutableMapOf<Int,String>()

    var tx = t
    if (!tx.endsWith("\n"))
    {
        // 改行で終わっていない場合追加しておく
        tx += "\n"
    }

    var tt: String
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
                    prompts.putIfAbsent(rownum, prompt)   // put at rownum , override if exist, add if absent
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
    var tx = getClipboardString()
    var prompts = analyzeText(tx)
    if (prompts.size < 2)
    {
        notPrompt(g)
        return
    }

    var tt = prompts.getOrDefault(-1, "")
    var maxsize = prompts.size - 1   // prompts.size = max num of prompts + headerblock, so -1
    for (i in 1..maxsize)
    {
        tt += prompts.getOrDefault(i,"")        // if num absent, skip with ""
    }
    g.textArea1.text = tt
    setClipboardString(tt)

}

fun convertText(g: gui)
{
    // add more lines... currently max 16 lines
    val sequence: Array<Array<Int>> = arrayOf(
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
        arrayOf(19, 2, 9, 12, 7, 21, 1, 5, 18, 6, 3, 20, 15, 10, 13, 4, 16, 17, 8, 11, 14)
    )


    var tx = getClipboardString()
    var prompts = analyzeText(tx)
    if (prompts.size < 2)
    {
        notPrompt(g)
        return
    }

    var j = 0
    var tt = prompts.getOrDefault(-1, "")
    var maxsize = prompts.size - 1   // prompts.size = max num of prompts + headerblock, so -1
    for (i in 0..maxsize-1)
    {
        //println("prompts $j: " + prompts.get(sequence[maxsize - 2][j]))
        tt += prompts.getOrDefault(sequence[maxsize - 2][j], "")  // why 2?  array starts 2
        j += 1
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