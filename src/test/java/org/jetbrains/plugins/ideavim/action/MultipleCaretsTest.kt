/*
 * IdeaVim - Vim emulator for IDEs based on the IntelliJ platform
 * Copyright (C) 2003-2022 The IdeaVim authors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.jetbrains.plugins.ideavim.action

import com.intellij.idea.TestFor
import com.maddyhome.idea.vim.VimPlugin
import com.maddyhome.idea.vim.action.motion.search.SearchWholeWordForwardAction
import com.maddyhome.idea.vim.api.injector
import com.maddyhome.idea.vim.command.SelectionType
import com.maddyhome.idea.vim.common.TextRange
import com.maddyhome.idea.vim.helper.VimBehaviorDiffers
import com.maddyhome.idea.vim.newapi.IjVimEditor
import com.maddyhome.idea.vim.options.OptionConstants
import com.maddyhome.idea.vim.options.OptionScope
import org.jetbrains.plugins.ideavim.SkipNeovimReason
import org.jetbrains.plugins.ideavim.TestWithoutNeovim
import org.jetbrains.plugins.ideavim.VimTestCase

/**
 * @author Vasily Alferov
 */
class MultipleCaretsTest : VimTestCase() {
  // com.maddyhome.idea.vim.action.visual.leftright
  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionLeftAction() {
    typeTextInFile(injector.parser.parseKeys("3h"), "abc${c}de${c}")
    assertState("${c}ab${c}cde")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionRightAction() {
    typeTextInFile(injector.parser.parseKeys("l"), "ab${c}cd${c}ef")
    assertState("abc${c}de${c}f")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMovementMerging() {
    val editor = typeTextInFile(injector.parser.parseKeys("2h"), "o${c}n${c}e")
    assertEquals(1, editor.caretModel.caretCount)
    assertState("${c}one")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionColumnAction() {
    typeTextInFile(
      injector.parser.parseKeys("4|"), """
     one${c} two
     three four fiv${c}e
     si${c}x seven${c}
     ${c}eig${c}ht nine ten${c}
     """.trimIndent()
    )
    assertState(
      """
    one${c} two
    thr${c}ee four five
    six${c} seven
    eig${c}ht nine ten
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionFirstColumnAction() {
    typeTextInFile(
      injector.parser.parseKeys("0"), """
     one${c} two
     three four fiv${c}e
     si${c}x seven${c}
     ${c}eig${c}ht nine te${c}n
     """.trimIndent()
    )
    assertState(
      """
    ${c}one two
    ${c}three four five
    ${c}six seven
    ${c}eight nine ten
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionFirstNonSpaceAction() {
    typeTextInFile(
      injector.parser.parseKeys("^"),
      """     one${c} two
three${c} four
  five${c} six
 ${c}  seven eight"""
    )
    assertState(
      """     ${c}one two
${c}three four
  ${c}five six
   ${c}seven eight"""
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionLastNonSpaceAction() {
    typeTextInFile(
      injector.parser.parseKeys("g_"), """one${c} two   
three${c} four      
 five si${c}x
seven eight    ${c}  
"""
    )
    assertState(
      """one tw${c}o   
three fou${c}r      
 five si${c}x
seven eigh${c}t      
"""
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionLastColumnAction() {
    typeTextInFile(
      injector.parser.parseKeys("$"), """
     one ${c}two
     three fou${c}r
     """.trimIndent()
    )
    assertState(
      """
    one tw${c}o
    three fou${c}r
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionLeftMatchCharAction() {
    typeTextInFile(injector.parser.parseKeys("2Fa"), "a${c}a${c}abab${c}ab${c}ab${c}b${c}x")
    assertState("${c}a${c}a${c}ab${c}ab${c}ababbx")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionRightMatchCharAction() {
    typeTextInFile(injector.parser.parseKeys("2fb"), "a${c}a${c}abab${c}ab${c}ab${c}b${c}x")
    assertState("aaaba${c}baba${c}b${c}b${c}x")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionLeftTillMatchCharAction() {
    typeTextInFile(injector.parser.parseKeys("2Ta"), "b${c}a${c}ba${c}a${c}a${c}ba${c}b")
    assertState("b${c}a${c}ba${c}a${c}a${c}bab")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionRightTillMatchCharAction() {
    typeTextInFile(
      injector.parser.parseKeys("2ta"),
      "${c}b${c}a${c}b${c}a${c}a${c}a${c}ba${c}b"
    )
    assertState("ba${c}b${c}a${c}a${c}a${c}ba${c}b")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionLastLeftMatchChar() {
    typeTextInFile(injector.parser.parseKeys("Fa;"), "a${c}a${c}abab${c}ab${c}ab${c}b${c}x")
    assertState("${c}aa${c}ab${c}ab${c}ababbx")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionLastRightMatchChar() {
    typeTextInFile(injector.parser.parseKeys("fb;"), "${c}a${c}aabab${c}ab${c}ab${c}b${c}x")
    assertState("aaaba${c}baba${c}b${c}b${c}x")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionLastRightTillMatchChar() {
    typeTextInFile(
      injector.parser.parseKeys("ta;"),
      "${c}b${c}a${c}b${c}a${c}a${c}a${c}ba${c}b"
    )
    assertState("ba${c}b${c}a${c}aa${c}ba${c}b")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionLastMatchCharReverse() {
    typeTextInFile(injector.parser.parseKeys("fa" + "2;" + "3,"), "abaab${c}a${c}baaa${c}abaaba")
    assertState("abaab${c}abaaa${c}abaaba")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionLeftWrap() {
    typeTextInFile(
      injector.parser.parseKeys("5<BS>"), """
     one
     t${c}wo three
     fo${c}ur
     
     """.trimIndent()
    )
    assertState("${c}one\ntwo thr${c}ee\nfour\n")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionRightWrap() {
    typeTextInFile(
      injector.parser.parseKeys("5<Space>"), """
     ${c}one
     two thr${c}ee
     four
     
     """.trimIndent()
    )
    assertState("one\nt${c}wo three\nfo${c}ur\n")
  }

  // com.maddyhome.idea.vim.action.visual.updown
  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionUpAction() {
    typeTextInFile(
      injector.parser.parseKeys("k"), """
     o${c}ne
     t${c}wo${c} 
     t${c}hree${c} 
     """.trimIndent()
    )
    assertState(
      """
    o${c}n${c}e
    t${c}wo${c} 
    three 
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionDownAction() {
    typeTextInFile(
      injector.parser.parseKeys("2j"),
      """
                o${c}n${c}e
                ${c}tw${c}o          ${c} 
                three
                four
                """.trimIndent()
    )
    assertState(
      """
    one
    two           
    t${c}h${c}ree
    ${c}fo${c}u${c}r
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testLeftRightAndUpDownMovements() {
    typeTextInFile(
      injector.parser.parseKeys("khj"), """
     abcde
     ab${c}cde
     abc${c}de
     abcd${c}e
     
     """.trimIndent()
    )
    assertState(
      """
    abcde
    a${c}bcde
    ab${c}cde
    abc${c}de
    
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionDownFirstNonSpaceAction() {
    typeTextInFile(
      injector.parser.parseKeys("+"),
      """ ${c} on${c}e${c} two
${c}   three${c} four
 five six
"""
    )
    assertState(
      """  one two
   ${c}three four
 ${c}five six
"""
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionDownLess1FirstNonSpaceActionWithNoCount() {
    typeTextInFile(
      injector.parser.parseKeys("_"),
      """     one${c} two
three${c} four
  five${c} six
 ${c}  seven eight"""
    )
    assertState(
      """     ${c}one two
${c}three four
  ${c}five six
   ${c}seven eight"""
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionDownLess1FirstNonSpaceActionWithCount() {
    typeTextInFile(
      injector.parser.parseKeys("3_"), """x${c}y${c}z
  skip this ${c}line
   don't skip this line
    stop there
"""
    )
    assertState(
      """xyz
  skip this line
   ${c}don't skip this line
    ${c}stop there
"""
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionUpFirstNonSpaceAction() {
    typeTextInFile(
      injector.parser.parseKeys("-"), """ one
${c}  tw${c}o
"""
    )
    assertState(
      """ ${c}one
  two
"""
    )
  }

  // com.maddyhome.idea.vim.action.visual.object
  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionInnerBigWordAction() {
    typeTextInFile(injector.parser.parseKeys("v" + "iW"), "a,${c}bc${c}d,e f,g${c}hi,j")
    assertState("<selection>a,bcd,e</selection> <selection>f,ghi,j</selection>")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionInnerWordAction() {
    typeTextInFile(injector.parser.parseKeys("v" + "iw"), "a,${c}bc${c}d,e f,g${c}hi,j")
    assertState("a,<selection>bcd</selection>,e f,<selection>ghi</selection>,j")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionInnerBlockAngleAction() {
    typeTextInFile(
      injector.parser.parseKeys("v" + "2i<"),
      "<asdf<asdf<a${c}sdf>a${c}sdf>asdf> <asdf<as${c}df>asdf>"
    )
    assertState("<<selection>asdf<asdf<asdf>asdf>asdf</selection>> <<selection>asdf<asdf>asdf</selection>>")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionInnerBlockBackQuoteActionWithNoCount() {
    typeTextInFile(
      injector.parser.parseKeys("v" + "i`"),
      "`as${c}d${c}f`asdf `a${c}sdf`a${c}sdf`a${c}sdf`"
    )
    assertState(
      "`<selection>asdf</selection>`asdf `<selection>asdf</selection>`<selection>asdf</selection>`<selection>asdf</selection>`"
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionInnerBlockBraceAction() {
    typeTextInFile(
      injector.parser.parseKeys("v" + "2i{"),
      "{asdf{asdf{a${c}sdf}a${c}sdf}asdf} {asdf{as${c}df}asdf}"
    )
    assertState("{<selection>asdf{asdf{asdf}asdf}asdf</selection>} {<selection>asdf{asdf}asdf</selection>}")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionInnerBlockBracketAction() {
    typeTextInFile(
      injector.parser.parseKeys("v" + "2i["),
      "[asdf[asdf[a${c}sdf]a${c}sdf]asdf] [asdf[as${c}df]asdf]"
    )
    assertState("[<selection>asdf[asdf[asdf]asdf]asdf</selection>] [<selection>asdf[asdf]asdf</selection>]")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionInnerBlockDoubleQuoteActionWithNoCount() {
    typeTextInFile(
      injector.parser.parseKeys("v" + "i\""),
      "\"as${c}d${c}f\"asdf \"a${c}sdf\"a${c}sdf\"a${c}sdf\""
    )
    assertState(
      "\"<selection>asdf</selection>\"asdf \"<selection>asdf</selection>\"<selection>asdf</selection>\"<selection>asdf</selection>\""
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionInnerBlockParenAction() {
    typeTextInFile(
      injector.parser.parseKeys("v" + "2i("),
      "(asdf(asdf(a${c}sdf)a${c}sdf)asdf) (asdf(as${c}df)asdf)"
    )
    assertState("(<selection>asdf(asdf(asdf)asdf)asdf</selection>) (<selection>asdf(asdf)asdf</selection>)")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionInnerBlockSingleQuoteActionWithNoCount() {
    typeTextInFile(
      injector.parser.parseKeys("v" + "i'"),
      "'as${c}d${c}f'asdf 'a${c}sdf'a${c}sdf'a${c}sdf'"
    )
    assertState(
      "'<selection>asdf</selection>'asdf '<selection>asdf</selection>'<selection>asdf</selection>'<selection>asdf</selection>'"
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionInnerBlockTagAction() {
    typeTextInFile(
      injector.parser.parseKeys("v" + "it"),
      """
                <asdf1>qwer<asdf2>qwer<asdf3>qw${c}er</asdf3>qw${c}er</asdf2>qwer</asdf1>
                <asdf1>qwer<asdf2>qw${c}er</asdf2>qwer</asdf1>
                """.trimIndent()
    )
    assertState(
      """
    <asdf1>qwer<asdf2><selection>qwer<asdf3>qwer</asdf3>qwer</selection></asdf2>qwer</asdf1>
    <asdf1>qwer<asdf2><selection>qwer</selection></asdf2>qwer</asdf1>
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionInnerParagraphAction() {
    typeTextInFile(
      injector.parser.parseKeys("v" + "3ip"),
      "a${c}bcd\na${c}bcd\n\nabcd\nabcd\n\na${c}bcd\nabcd\n\nabcd\nabcd\n"
    )
    assertState("<selection>abcd\nabcd\n\nabcd\nabcd\n</selection>\n<selection>abcd\nabcd\n\nabcd\nabcd\n</selection>")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionInnerSentenceAction() {
    typeTextInFile(injector.parser.parseKeys("v" + "3is"), "a${c}bcd a${c}bcd. abcd abcd. a${c}bcd abcd.")
    assertState("<selection>abcd abcd. abcd abcd.</selection><selection> abcd abcd.</selection>")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionOuterBigWordAction() {
    typeTextInFile(
      injector.parser.parseKeys("v" + "aW"),
      " a${c}bcd${c}e.abcde.a${c}bcde  a${c}bcde.abcde\n"
    )
    assertState(" <selection>abcde.abcde.abcde  </selection><selection>abcde.abcde</selection>\n")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionOuterWordAction() {
    typeTextInFile(
      injector.parser.parseKeys("v" + "aw"),
      " a${c}bcd${c}e.abcde.a${c}bcde  a${c}bcde.abcde"
    )
    assertState(" <selection>abcde</selection>.abcde.<selection>abcde  abcde</selection>.abcde")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionOuterBlockAngleAction() {
    typeTextInFile(
      injector.parser.parseKeys("v" + "2a<"),
      "<asdf<asdf<a${c}sdf>a${c}sdf>asdf> <asdf<a${c}sdf>asdf>"
    )
    assertState("<selection><asdf<asdf<asdf>asdf>asdf></selection> <selection><asdf<asdf>asdf></selection>")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionOuterBlockBackQuoteAction() {
    typeTextInFile(
      injector.parser.parseKeys("v" + "a`"),
      "`asdf`asdf`a${c}sdf`a${c}sdf`asdf` `asdf`a${c}sdf`asdf`"
    )
    assertState("`asdf`asdf<selection>`asdf`asdf`</selection>asdf` `asdf<selection>`asdf`</selection>asdf`")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionOuterBraceAction() {
    typeTextInFile(
      injector.parser.parseKeys("v" + "2a{"),
      "{asdf{asdf{a${c}sdf}a${c}sdf}asdf} {asdf{a${c}sdf}asdf}"
    )
    assertState("<selection>{asdf{asdf{asdf}asdf}asdf}</selection> <selection>{asdf{asdf}asdf}</selection>")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionOuterBlockBracketAction() {
    typeTextInFile(
      injector.parser.parseKeys("v" + "2a["),
      "[asdf[asdf[a${c}sdf]a${c}sdf]asdf] [asdf[a${c}sdf]asdf]"
    )
    assertState("<selection>[asdf[asdf[asdf]asdf]asdf]</selection> <selection>[asdf[asdf]asdf]</selection>")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionOuterBlockDoubleQuoteAction() {
    typeTextInFile(
      injector.parser.parseKeys("v" + "a\""),
      "\"asdf\"asdf\"a${c}sdf\"a${c}sdf\"asdf\" \"asdf\"a${c}sdf\"asdf\""
    )
    assertState("\"asdf\"asdf<selection>\"asdf\"asdf\"</selection>asdf\" \"asdf<selection>\"asdf\"</selection>asdf\"")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionOuterBlockParenAction() {
    typeTextInFile(
      injector.parser.parseKeys("v" + "2a("),
      "(asdf(asdf(a${c}sdf)a${c}sdf)asdf) (asdf(a${c}sdf)asdf)"
    )
    assertState("<selection>(asdf(asdf(asdf)asdf)asdf)</selection> <selection>(asdf(asdf)asdf)</selection>")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionOuterBlockSingleQuoteAction() {
    typeTextInFile(
      injector.parser.parseKeys("v" + "a'"),
      "'asdf'asdf'a${c}sdf'a${c}sdf'asdf' 'asdf'a${c}sdf'asdf'"
    )
    assertState("'asdf'asdf<selection>'asdf'asdf'</selection>asdf' 'asdf<selection>'asdf'</selection>asdf'")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionOuterBlockTagAction() {
    typeTextInFile(
      injector.parser.parseKeys("v" + "at"),
      """
                <asdf1>qwer<asdf2>qwer<asdf3>qw${c}er</asdf3>qw${c}er</asdf2>qwer</asdf1>
                <asdf1>qwer<asdf2>qw${c}er</asdf2>qwer</asdf1>
                """.trimIndent()
    )
    assertState(
      """
    <asdf1>qwer<selection><asdf2>qwer<asdf3>qwer</asdf3>qwer</asdf2></selection>qwer</asdf1>
    <asdf1>qwer<selection><asdf2>qwer</asdf2></selection>qwer</asdf1>
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionOuterParagraphAction() {
    typeTextInFile(injector.parser.parseKeys("v" + "2ap"), "a${c}sdf\n\na${c}sdf\n\nasdf\n\n")
    assertState("<selection>asdf\n\nasdf\n\nasdf\n\n</selection>")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionOuterSentenceAction() {
    typeTextInFile(injector.parser.parseKeys("v" + "2as"), "a${c}sdf. a${c}sdf. asdf.")
    assertState("<selection>asdf. asdf. asdf.</selection>")
  }

  // com.maddyhime.idea.vim.action.visual.text
  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionBigWordEndLeftAction() {
    typeTextInFile(injector.parser.parseKeys("gE"), "a.asdf. a${c}sdf${c}.a a; as${c}df\n a${c}sdf")
    assertState("a.asdf${c}. asdf.a a${c}; asd${c}f\n asdf")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionBigWordEndRightAction() {
    typeTextInFile(
      injector.parser.parseKeys("E"),
      "a${c}.as${c}df. a${c}s${c}df.a ${c} a; as${c}df"
    )
    assertState("a.asdf${c}. asdf.${c}a  a${c}; asd${c}f")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionBigWordLeftAction() {
    typeTextInFile(injector.parser.parseKeys("B"), "a${c}.as${c}df. a${c}sdf.a ${c} a; as${c}df")
    assertState("${c}a.asdf. ${c}asdf.a  a; ${c}asdf")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionBigWordRightAction() {
    typeTextInFile(injector.parser.parseKeys("W"), "a${c}.as${c}df. a${c}sdf.a ${c} a; as${c}df")
    assertState("a.asdf. ${c}asdf.a  ${c}a; asd${c}f")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionWordEndLeftAction() {
    typeTextInFile(injector.parser.parseKeys("ge"), "a.asdf. a${c}sdf${c}.a a; as${c}df\n a${c}sdf")
    assertState("a.asdf${c}. asd${c}f.a a${c}; asd${c}f\n asdf")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionWordEndRightAction() {
    typeTextInFile(
      injector.parser.parseKeys("e"),
      "a${c}.as${c}df. a${c}s${c}df.a ${c} a; as${c}df"
    )
    assertState("a.asd${c}f. asd${c}f.a  ${c}a; asd${c}f")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionWordLeftAction() {
    typeTextInFile(injector.parser.parseKeys("b"), "a${c}.as${c}df. a${c}sdf.a ${c} a; as${c}df")
    assertState("${c}a.${c}asdf. ${c}asdf.${c}a  a; ${c}asdf")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionWordRightAction() {
    typeTextInFile(injector.parser.parseKeys("w"), "a${c}.as${c}df. a${c}sdf.a ${c} a; as${c}df")
    assertState("a.${c}asdf${c}. asdf${c}.a  ${c}a; asd${c}f")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionCamelEndLeftAction() {
    typeTextInFile(
      injector.parser.parseKeys("2]b"),
      "ClassName.M${c}ethodN${c}ame(${c}Arg1Type arg1Na${c}me, Arg2Type arg2${c}Name) ${c}{"
    )
    assertState(
      "Clas${c}sNam${c}e.Metho${c}dName(Arg1Type ar${c}g1Name, Arg2Type ar${c}g${c}2Name) {"
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionCamelEndRightAction() {
    typeTextInFile(
      injector.parser.parseKeys("]w"),
      "Cl${c}assName.M${c}ethodN${c}ame(${c}Arg1Type arg1Na${c}me, Arg2Type ar${c}g2${c}Name) {"
    )
    assertState(
      "Clas${c}sName.Metho${c}dNam${c}e(Ar${c}g1Type arg1Nam${c}e, Arg2Type arg${c}2Nam${c}e) {"
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionCamelLeftAction() {
    typeTextInFile(
      injector.parser.parseKeys("2[b"),
      "ClassName.M${c}ethodN${c}ame(${c}Arg1Type arg1Na${c}me, Arg2Type arg2${c}Name) ${c}{"
    )
    assertState("Class${c}Name.${c}MethodName(Arg1Type arg${c}1Name, Arg2Type ${c}arg${c}2Name) {")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionCamelRightAction() {
    typeTextInFile(
      injector.parser.parseKeys("[w"),
      "Cl${c}assName.M${c}ethodN${c}ame(${c}Arg1Type arg1Na${c}me, Arg2Type ar${c}g2Name) {"
    )
    assertState(
      "Class${c}Name.Method${c}Name(${c}Arg${c}1Type arg1Name, ${c}Arg2Type arg${c}2Name) {"
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionMethodNextEndAction() {
    configureByJavaText(
      """public class Foo {
    private static void firstMethod(int argument) {
        // Do som${c}ething...
    }
    ${c}private static int x${c};    private static void secondMethod(String argument) {
        // Do something.${c}..
    }
}"""
    )
    typeText(injector.parser.parseKeys("]M"))
    assertState(
      """public class Foo {
    private static void firstMethod(int argument) {
        // Do something...
    ${c}}
    private static int x${c};    private static void secondMethod(String argument) {
        // Do something...
    ${c}}
}"""
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionMethodNextStartAction() {
    configureByJavaText(
      """public class Foo {
 ${c}   private static void firstMethod(int argument) {
        // Do som${c}ething...
    }
    ${c}private static int x${c};    private static void secondMethod(String argument) {
        // Do something.${c}..
    }
}"""
    )
    typeText(injector.parser.parseKeys("]m"))
    assertState(
      """public class Foo {
    private static void firstMethod(int argument) ${c}{
        // Do something...
    }
    ${c}private static int x;    private static void secondMethod(String argument) ${c}{
        // Do something...
    }
}"""
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionMethodPreviousEndAction() {
    configureByJavaText(
      """public class Foo {
 ${c}   private static void firstMethod(int argument) {
        // Do som${c}ething...
    }
    ${c}private static int x${c};    private static void secondMethod(String argument) {
        // Do something.${c}..
    }
}"""
    )
    typeText(injector.parser.parseKeys("[M"))
    assertState(
      """public class Foo {
    private static void firstMethod(int argument) {
        // Do something...
    ${c}}
    private static int x${c};    private static void secondMethod(String argument) {
        // Do something...
    }
}"""
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionMethodPreviousStartAction() {
    configureByJavaText(
      """public class Foo {
 ${c}   private static void firstMethod(int argument) {
        // Do som${c}ething...
    }
    ${c}private static int x${c};    private static void secondMethod(String argument) {
        // Do something.${c}..
    }
}"""
    )
    typeText(injector.parser.parseKeys("[m"))
    assertState(
      """public class Foo ${c}{
    private static void firstMethod(int argument) ${c}{
        // Do something...
    }
    ${c}private static int x;    private static void secondMethod(String argument) ${c}{
        // Do something...
    }
}"""
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionNthCharacterAction() {
    typeTextInFile(
      injector.parser.parseKeys("5" + "go"),
      "${c}on${c}e two thr${c}ee four fiv${c}e six seven eigh${c}t ni${c}ne ten"
    )
    assertState("one ${c}two three four five six seven eight nine ten")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionParagraphNextAction() {
    typeTextInFile(injector.parser.parseKeys("2}"), "o${c}ne\n\n${c}two\n\nthree\nthree\n\nfour\n\nfive")
    assertState("one\n\ntwo\n${c}\nthree\nthree\n${c}\nfour\n\nfive")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionParagraphPreviousAction() {
    typeTextInFile(injector.parser.parseKeys("2{"), "one\n\ntwo\n\nthree\nthree\n\nfou${c}r\n\nfi${c}ve")
    assertState("one\n\ntwo\n${c}\nthree\nthree\n${c}\nfour\n\nfive")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionSectionBackwardEndAction() {
    typeTextInFile(
      injector.parser.parseKeys("[]"), """
     no${c}t_a_brace
     {
     ${c}not_a_brace
     }
     {
     n${c}ot_a_brace
     }
     not_a_${c}brace
     """.trimIndent()
    )
    assertState(
      """
    ${c}not_a_brace
    {
    not_a_brace
    ${c}}
    {
    not_a_brace
    ${c}}
    not_a_brace
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionSectionBackwardStartAction() {
    typeTextInFile(
      injector.parser.parseKeys("[["), """
     n${c}ot_a_brace
     {
     not_a_${c}brace
     ${c}}
     {
     not_a_b${c}race
     ${c}}
     not_a_brace
     """.trimIndent()
    )
    assertState(
      """
    ${c}not_a_brace
    ${c}{
    not_a_brace
    }
    ${c}{
    not_a_brace
    }
    not_a_brace
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionSectionForwardEndAction() {
    typeTextInFile(
      injector.parser.parseKeys("]["), """
     n${c}ot_a_brace
     {
     n${c}ot_a_brace
     ${c}}
     {
     not_${c}a_brace
     }
     not_a_brace
     """.trimIndent()
    )
    assertState(
      """
    not_a_brace
    {
    not_a_brace
    ${c}}
    {
    not_a_brace
    ${c}}
    not_a_brace
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionSectionForwardStartAction() {
    typeTextInFile(
      injector.parser.parseKeys("]]"), """
     n${c}ot_a_brace
     {
     n${c}ot_a_brace
     ${c}}
     {
     not_a_brace
     }
     not_a_brace
     """.trimIndent()
    )
    assertState(
      """
    not_a_brace
    ${c}{
    not_a_brace
    }
    ${c}{
    not_a_brace
    }
    not_a_brace
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionSentenceNextEndAction() {
    typeTextInFile(
      injector.parser.parseKeys("g)"),
      "a${c}sdf${c}. a${c}sdf. a${c}sdf.${c} asdf.${c} asdf."
    )
    assertState("asdf${c}. asdf${c}. asdf${c}. asdf${c}. asdf${c}.")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionSentenceNextStartAction() {
    typeTextInFile(injector.parser.parseKeys(")"), "a${c}sdf. ${c}asdf.${c} asdf. ${c}asdf. asdf.")
    assertState("asdf. ${c}asdf. ${c}asdf. asdf. ${c}asdf.")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionSentencePreviousEndAction() {
    typeTextInFile(
      injector.parser.parseKeys("g("),
      "asdf.${c} a${c}sdf${c}. as${c}df. asd${c}f. ${c}asdf."
    )
    assertState("asdf${c}. asdf${c}. asdf${c}. asdf${c}. asdf.")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionSentencePreviousStartAction() {
    typeTextInFile(
      injector.parser.parseKeys("("),
      "asd${c}f. ${c}as${c}df. asdf${c}. asdf${c}. as${c}df."
    )
    assertState("${c}asdf. ${c}asdf. ${c}asdf. ${c}asdf. ${c}asdf.")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionUnmatchedBraceCloseAction() {
    typeTextInFile(injector.parser.parseKeys("]}"), "{{}${c} }${c} }${c} {}}${c}{}}")
    assertState("{{} ${c}} ${c}} {}${c}}{${c}}}")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionUnmatchedBraceOpenAction() {
    typeTextInFile(injector.parser.parseKeys("[{"), "{${c} {{}${c} }{${c}}{${c}} ")
    assertState("${c}{ ${c}{{} }${c}{}${c}{} ")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionUnmatchedParenCloseAction() {
    typeTextInFile(injector.parser.parseKeys("])"), "(()${c} )${c} )${c} ())${c}())")
    assertState("(() ${c}) ${c}) ()${c})(${c}))")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionUnmatchedParenOpenAction() {
    typeTextInFile(injector.parser.parseKeys("[("), "(${c} (()${c} )(${c})(${c}) ")
    assertState("${c}( ${c}(() )${c}()${c}() ")
  }

  // com.maddyhome.idea.vim.action.visual.visual
  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testVisualSwapEndsAction() {
    typeTextInFile(injector.parser.parseKeys("v" + "iw" + "o"), "o${c}ne ${c}two th${c}ree\n")
    assertState(
      "<selection>${c}one</selection> <selection>${c}two</selection> <selection>${c}three</selection>\n"
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testVisualToggleCharacterMode() {
    typeTextInFile(injector.parser.parseKeys("v" + "e"), "o${c}ne ${c}two th${c}ree")
    assertState("o<selection>ne</selection> <selection>two</selection> th<selection>ree</selection>")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testVisualToggleLineMode() {
    typeTextInFile(
      injector.parser.parseKeys("V" + "2k"), """
     one two
     three four
     fi${c}ve six
     seven eight
     nine ten
     eleven twelve
     th${c}irteen fourteen
     
     """.trimIndent()
    )
    assertState(
      """
    <selection>one two
    three four
    five six
    </selection>seven eight
    <selection>nine ten
    eleven twelve
    thirteen fourteen
    </selection>
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testVisualModeMerging() {
    typeTextInFile(
      injector.parser.parseKeys("V" + "j"), """
     one${c} two
     thr${c}ee four
     five six
     
     """.trimIndent()
    )
    assertState(
      """
    <selection>one two
    three four
    five six
    </selection>
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testVisualCharacterToVisualLineModeSwitch() {
    typeTextInFile(
      injector.parser.parseKeys("v" + "k" + "V"),
      """
                one two
                three fo${c}ur
                five six
                seven eight
                nine t${c}en
                
                """.trimIndent()
    )
    assertState(
      """
    <selection>one two
    three four
    </selection>five six
    <selection>seven eight
    nine ten
    </selection>
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testVisualLineToVisualCharacterModeSwitch() {
    typeTextInFile(
      injector.parser.parseKeys("V" + "k" + "v"),
      """
                one two
                thre${c}e four
                five six
                seven eight
                n${c}ine ten
                
                """.trimIndent()
    )
    assertState(
      """
    one <selection>two
    three</selection> four
    five six
    s<selection>even eight
    ni</selection>ne ten
    
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testVisualBlockDownAfterLineEndMovement() {
    typeTextInFile(injector.parser.parseKeys("<C-V>\$j"), "abc\ndef\n")
    assertState(
      """
    <selection>abc</selection>
    <selection>def</selection>
    
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testVisualBlockDownMovementAfterShorterLineAction() {
    typeTextInFile(
      injector.parser.parseKeys("<C-V>" + "kkjj"), """
     one
     
     two three
     four fi${c}ve
     
     """.trimIndent()
    )
    assertState(
      """
    one
    
    two three
    four fi<selection>${c}v</selection>e
    
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testVisualBlockDownMovementWithEmptyLineInMiddle() {
    typeTextInFile(
      injector.parser.parseKeys("<C-V>" + "3k" + "j"), """
     one
     
     two three
     four fi${c}ve
     
     """.trimIndent()
    )
    assertState(
      """
    one
    
    <selection>two thre</selection>e
    <selection>four fiv</selection>e
    
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testVisualBlockDownMovementWithManyEmptyLinesInMiddle() {
    typeTextInFile(
      injector.parser.parseKeys("<C-V>" + "4kjjj"), """
     one
     
     
     two three
     four fi${c}ve
     
     """.trimIndent()
    )
    assertState(
      """
    one
    
    
    two thr<selection>e</selection>e
    four fi<selection>v</selection>e
    
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMergingSelections() {
    typeTextInFile(
      injector.parser.parseKeys("v" + "aW" + "l" + "h"),
      "a${c}bcde.abcde.abcde  ab${c}cde.abcde\n"
    )
    assertState("<selection>abcde.abcde.abcde  abcde.abcde</selection>\n")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testVisualMotionUp() {
    typeTextInFile(injector.parser.parseKeys("v" + "k" + "k"), "abcde\nabcde\nab${c}cde\n")
    assertState("ab<selection>cde\nabcde\nabc</selection>de\n")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testVisualMotionDown() {
    typeTextInFile(injector.parser.parseKeys("v" + "2j" + "j"), "ab${c}cde\nabcde\n\nabcde\n")
    assertState("ab<selection>cde\nabcde\n\nabc</selection>de\n")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testVisualLineMotionUp() {
    typeTextInFile(injector.parser.parseKeys("V" + "2k" + "k"), "abcde\nabcde\n\nab${c}cde\nabcde\n")
    assertState("<selection>ab${c}cde\nabcde\n\nabcde\n</selection>abcde\n")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testVisualLineMotionDown() {
    typeTextInFile(injector.parser.parseKeys("V" + "2j" + "j"), "ab${c}cde\nabcde\n\nabcde\nabcde\n")
    assertState("<selection>abcde\nabcde\n\nab${c}cde\n</selection>abcde\n")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testVisualCharacterUpMerging() {
    typeTextInFile(injector.parser.parseKeys("v" + "2k" + "k"), "abcde\nabcde\n\nabc${c}de\nab${c}cde\n")
    assertState("abc<selection>${c}de\nabcde\n\nabcde\nabc</selection>de\n")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testVisualCharacterDownMerging() {
    typeTextInFile(injector.parser.parseKeys("v" + "2j" + "j"), "abc${c}de\nab${c}cde\n\nabcde\nabcde\n")
    assertState("abc<selection>de\nabcde\n\nabcde\nab${c}c</selection>de\n")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testVisualLineUpMerging() {
    typeTextInFile(injector.parser.parseKeys("V" + "2k" + "k"), "abcde\nabcde\n\nabc${c}de\nab${c}cde\n")
    assertState("<selection>abc${c}de\nabcde\n\nabcde\nabcde\n</selection>")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testVisualLineDownMerging() {
    typeTextInFile(injector.parser.parseKeys("V" + "2j" + "j"), "abc${c}de\nab${c}cde\n\nabcde\nabcde\n")
    assertState("<selection>abcde\nabcde\n\nabcde\nab${c}cde\n</selection>")
  }

  // com.maddyhome.idea.vim.action.change.change
  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testAutoIndentLinesVisualAction() {
    configureByJavaText(
      """${c}public class Foo {
private boolean x;
                         private boolean y;
private boolean z;
${c}public void foo() {
x = true; // This will be indented
}
public void bar() {
y = true; // And this will not
}
}
"""
    )
    typeText(injector.parser.parseKeys("V2j="))
    assertState(
      """${c}public class Foo {
    private boolean x;
    private boolean y;
private boolean z;
    ${c}public void foo() {
        x = true; // This will be indented
    }
public void bar() {
y = true; // And this will not
}
}
"""
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testChangeCaseLowerMotionAction() {
    typeTextInFile(
      injector.parser.parseKeys("gu2w"),
      "O${c}NcE thIs ${c}TEXt wIlL n${c}Ot lOoK s${c}O rIdIcuLoUs\n"
    )
    assertState("O${c}nce this ${c}text will n${c}ot look s${c}o ridiculous\n")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testChangeCaseLowerVisualAction() {
    typeTextInFile(
      injector.parser.parseKeys("v2wu"),
      "O${c}NcE thIs ${c}TEXt wIlL n${c}Ot lOoK s${c}O rIdIcuLoUs\n"
    )
    assertState("O${c}nce this text will n${c}ot look s${c}o ridiculous\n")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testChangeCaseToggleCharacterAction() {
    typeTextInFile(injector.parser.parseKeys("5~"), "OnE t${c}Wo ${c}ThReE${c} fOuR fIvE\n")
    assertState("OnE twO Th${c}rEe${c} FoUr${c} fIvE\n")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testChangeCaseToggleMotionAction() {
    typeTextInFile(injector.parser.parseKeys("g~e"), "${c}capitalize ${c}UNCAPITALIZE${c} ${c}sTaY\n")
    assertState("${c}CAPITALIZE ${c}uncapitalize${c} ${c}sTaY\n")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testChangeCaseToggleVisualAction() {
    typeTextInFile(injector.parser.parseKeys("ve~"), "${c}capitalize ${c}UNCAPITALIZE\n")
    assertState("${c}CAPITALIZE ${c}uncapitalize\n")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testChangeCaseUpperMotionAction() {
    typeTextInFile(
      injector.parser.parseKeys("gU2w"),
      "O${c}NcE thIs ${c}TEXt wIlL ${c}nOt lOoK ${c}sO rIdIcuLoUs\n"
    )
    assertState("O${c}NCE THIS ${c}TEXT WILL ${c}NOT LOOK ${c}SO RIDICULOUS\n")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testChangeCaseUpperVisualAction() {
    typeTextInFile(
      injector.parser.parseKeys("v2wU"),
      "O${c}NcE thIs ${c}TEXt wIlL N${c}Ot lOoK S${c}O rIdIcuLoUs\n"
    )
    assertState("O${c}NCE THIS TEXT WILL N${c}OT LOOK S${c}O RIDICULOUS\n")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testChangeCharacterAction() {
    typeTextInFile(injector.parser.parseKeys("rz"), "on${c}e ${c}t${c}w${c}o th${c}r${c}ee")
    assertState("on${c}z ${c}z${c}z${c}z th${c}z${c}ze")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testChangeCharacterActionWithCount() {
    typeTextInFile(injector.parser.parseKeys("2rz"), "on${c}e ${c}t${c}w${c}o th${c}r${c}ee")
    assertState("on${c}zz${c}z${c}z${c}zzth${c}z${c}zz")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testChangeCharactersAction() {
    typeTextInFile(injector.parser.parseKeys("4s" + "<ESC>"), "on${c}e two ${c}th${c}ee four five\n")
    assertState("o${c}no${c} r five\n")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testChangeEndOfLineAction() {
    typeTextInFile(
      injector.parser.parseKeys("Cabc" + "<ESC>"),
      """
                a${c}bcde
                abcde
                a${c}bcde
                a${c}bcd${c}e
                abcde
                
                """.trimIndent()
    )
    assertState(
      """
    aab${c}c
    abcde
    aab${c}c
    aab${c}c
    abcde
    
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testChangeLineAction() {
    typeTextInFile(
      injector.parser.parseKeys("c2ca" + "<ESC>"), """
     ab${c}cde
     abcde
     abcde
     abc${c}de
     abcde
     
     """.trimIndent()
    )
    assertState(
      """
    ${c}a
    abcde
    ${c}a
    
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testOneCaretPositionAfterChangeLineAction() {
    typeTextInFile(
      injector.parser.parseKeys("c2c" + "<ESC>"), """
     abcde
     ab${c}cde
     abcde
     abcde
     
     """.trimIndent()
    )
    assertState(
      """
    abcde
    ${c}
    abcde
    
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testCaretPositionAfterChangeLineAction() {
    typeTextInFile(
      injector.parser.parseKeys("c2c" + "<ESC>"), """
     abcd${c}e
     abcde
     abcde
     ab${c}cde
     abcde
     abcde
     ${c}abcde
     abcde
     
     """.trimIndent()
    )
    assertState(
      """
    ${c}
    abcde
    ${c}
    abcde
    ${c}
    
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testChangeMotionAction() {
    typeTextInFile(
      injector.parser.parseKeys("ciw" + "correct" + "<ESC>"),
      "correct correct wron${c}g wr${c}ong correct\n"
    )
    assertState("correct correct correc${c}t correc${c}t correct\n")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testChangeNumberIncAction() {
    typeTextInFile(injector.parser.parseKeys("<C-A>"), "1${c}7${c}7 2${c}38 ${c}999\n")
    assertState("17${c}9 23${c}9 100${c}0\n")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testChangeNumberDecAction() {
    typeTextInFile(injector.parser.parseKeys("<C-X>"), "1${c}8${c}1 2${c}40 ${c}1001\n")
    assertState("17${c}9 23${c}9 100${c}0\n")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testChangeReplaceAction() {
    typeTextInFile(
      injector.parser.parseKeys("Rz" + "<ESC>"),
      "on${c}e ${c}t${c}w${c}o th${c}r${c}ee"
    )
    assertState("on${c}z ${c}z${c}z${c}z th${c}z${c}ze")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testChangeReplaceActionWithSeveralCharacters() {
    val before = """
            ${c}qwe
            asd ${c}zxc
            qwe${c}asdzxc
            """.trimIndent()
    typeTextInFile(injector.parser.parseKeys("Rrty" + "<Esc>"), before)
    val after = """
            rt${c}y
            asd rt${c}y
            qwert${c}yzxc
            """.trimIndent()
    assertState(after)
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testChangeVisualCharacterAction() {
    typeTextInFile(injector.parser.parseKeys("v2lra"), "abcd${c}ffffff${c}abcde${c}aaaa\n")
    assertState("abcdaa${c}afffaa${c}adeaa${c}aa\n")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testChangeVisualLinesAction() {
    typeTextInFile(
      injector.parser.parseKeys("VjS" + "abcde" + "<ESC>"),
      """
                gh${c}ijk
                ghijk
                abcde
                ghi${c}jk
                ghijk
                
                """.trimIndent()
    )
    assertState(
      """
    abcd${c}e
    abcde
    abcd${c}e
    
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testChangeVisualLinesEndAction() {
    typeTextInFile(
      injector.parser.parseKeys("vjC" + "abcde" + "<ESC>"),
      """
                gh${c}ijk
                ghijk
                abcde
                ghi${c}jk
                ghijk
                
                """.trimIndent()
    )
    assertState(
      """
    abcd${c}e
    abcde
    abcd${c}e
    
    """.trimIndent()
    )
  }

  // com.maddyhome.idea.vim.action.change.delete
  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testDeleteCharacterAction() {
    typeTextInFile(
      injector.parser.parseKeys("<Del>"), """
     a${c}bcde
     ${c}abcde
     abcd${c}e
     
     """.trimIndent()
    )
    assertState(
      """
    a${c}cde
    ${c}bcde
    abc${c}d
    
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testDeleteCharacterActionOrder() {
    typeTextInFile(injector.parser.parseKeys("<Del>"), "ab${c}c${c}d${c}e abcde\n")
    assertState("ab${c} abcde\n")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testDeleteCharacterLeftAction() {
    typeTextInFile(
      injector.parser.parseKeys("3X"), """
     a${c}bcde
     ${c}abcde
     abcd${c}e
     
     """.trimIndent()
    )
    assertState(
      """
    ${c}bcde
    ${c}abcde
    a${c}e
    
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testDeleteCharacterLeftCaretMerging() {
    typeTextInFile(injector.parser.parseKeys("3X"), "a${c}bc${c}def${c}ghij${c}klmn${c}op${c}q")
    assertState("gq")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testDeleteCharacterRightAction() {
    typeTextInFile(
      injector.parser.parseKeys("3x"), """
     a${c}bcde
     ${c}abcde
     abcd${c}e
     
     """.trimIndent()
    )
    assertState(
      """
    a${c}e
    ${c}de
    abc${c}d
    
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testDeleteCharacterRightCaretMerging() {
    typeTextInFile(injector.parser.parseKeys("4x"), "o${c}ne ${c}two ${c}three four")
    assertState("o${c} four")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testDeleteEndOfLineAction() {
    typeTextInFile(
      injector.parser.parseKeys("D"), """
     abcd${c}e
     abcde
     abc${c}de
     ${c}abcde
     ab${c}cde
     ab${c}cd${c}e
     
     """.trimIndent()
    )
    assertState(
      """
    abc${c}d
    abcde
    ab${c}c
    ${c}
    a${c}b
    a${c}b
    
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testDeleteEndOfLineActionWithCount() {
    typeTextInFile(
      injector.parser.parseKeys("3D"), """
     ab${c}cde
     abcde
     abcde
     abcd${c}e
     a${c}bcd${c}e
     abc${c}de
     
     """.trimIndent()
    )
    assertState(
      """
    ab
    abcd
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testDeleteJoinLinesAction() {
    typeTextInFile(
      injector.parser.parseKeys("gJ"), """ab${c}cde
abcde
ab${c}cde
abcd${c}e
abcde
abc${c}de
  abcde
"""
    )
    assertState(
      """
    abcde${c}abcde
    abcde${c}abcde${c}abcde
    abcde${c}  abcde
    
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testDeleteJoinLinesSimpleAction() {
    typeTextInFile(
      injector.parser.parseKeys("gJ"), """
     a${c}bcde
     abcde
     
     """.trimIndent()
    )
    assertState("abcde${c}abcde\n")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testDeleteJoinLinesSpacesAction() {
    typeTextInFile(
      injector.parser.parseKeys("J"), """ab${c}cde
abcde
ab${c}cde
abcd${c}e
abcde
abc${c}de
  abcde
"""
    )
    assertState(
      """
    abcde${c} abcde
    abcde${c} abcde${c} abcde
    abcde${c} abcde
    
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testDeleteJoinVisualLinesAction() {
    typeTextInFile(
      injector.parser.parseKeys("VkgJ"), """
     one
     tw${c}o
     three
     fo${c}ur
     
     """.trimIndent()
    )
    assertState(
      """
    one${c}two
    three${c}four
    
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testDeleteJoinVisualLinesSpacesAction() {
    typeTextInFile(
      injector.parser.parseKeys("VkJ"), """
     abcde
     abcd${c}e
     abcde
     ab${c}cde
     
     """.trimIndent()
    )
    assertState(
      """
    abcde${c} abcde
    abcde${c} abcde
    
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testDeleteVisualAction() {
    typeTextInFile(
      injector.parser.parseKeys("vlj"), """
     abc${c}de
     ${c}abcde
     abc${c}de
     abcde
     
     """.trimIndent()
    )
    assertState(
      """
                abc<selection>de
                abcde
                a${c}b</selection>c<selection>de
                abcd${c}e</selection>
                
                """.trimIndent()
    )
    typeText(injector.parser.parseKeys("d"))
    assertState("abc${c}c\n")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testDeleteVisualActionWithMultipleCaretsLeft() {
    typeTextInFile(
      injector.parser.parseKeys("v" + "fd" + "d"), """
     a${c}bcde
     abcde
     ${c}abcde
     ab${c}cde
     
     """.trimIndent()
    )
    assertState(
      """
    a${c}e
    abcde
    ${c}e
    ab${c}e
    
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testDeleteVisualLinesAction() {
    typeTextInFile(
      injector.parser.parseKeys("Vjd"), """
     abc${c}de
     abcde
     abcde
     a${c}bcde
     abcde
     
     """.trimIndent()
    )
    assertState("${c}abcde\n${c}")
  }

  // com.maddyhome.idea.vim.action.change.insert
  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testInsertEscape() {
    typeTextInFile(injector.parser.parseKeys("i" + "<ESC>" + "i" + "<ESC>"), "on${c}e tw${c}o th${c}ree")
    assertState("${c}one ${c}two ${c}three")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testInsertAfterCursorActionMovement() {
    typeTextInFile(injector.parser.parseKeys("a" + "<ESC>"), "on${c}e two th${c}ree")
    assertState("on${c}e two th${c}ree")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testInsertAfterCursorAction() {
    typeTextInFile(injector.parser.parseKeys("a" + "abcd" + "<ESC>"), "on${c}e two th${c}re${c}e")
    assertState("oneabc${c}d two thrabc${c}deeabc${c}d")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testInsertBeforeCursorAction() {
    typeTextInFile(
      injector.parser.parseKeys("i" + "four" + "<ESC>"), """
     one two three ${c} 
     seven six five ${c} 
     
     """.trimIndent()
    )
    assertState(
      """
    one two three fou${c}r 
    seven six five fou${c}r 
    
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testInsertBeforeFirstNonBlankAction() {
    typeTextInFile(
      injector.parser.parseKeys("I" + "four " + "<ESC>"),
      """  three t${c}wo on${c}e
${c} five six se${c}ven eight
"""
    )
    assertState(
      """  four${c} three two one
 four${c} five six seven eight
"""
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testInsertCharacterAboveCursorAction() {
    typeTextInFile(
      injector.parser.parseKeys("a" + "<C-Y>" + "<C-Y>" + "<C-Y>" + "<ESC>"), """ one two three four
${c}  two three four
four three two one
four three two${c} 
"""
    )
    assertState(
      """ one two three four
 on${c}e two three four
four three two one
four three two on${c}e
"""
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testInsertCharacterBelowCursorAction() {
    typeTextInFile(
      injector.parser.parseKeys("a" + "<C-E>" + "<C-E>" + "<C-E>" + "<ESC>"), """${c}  two three four
 one two three four
four three two${c} 
four three two one
"""
    )
    assertState(
      """ on${c}e two three four
 one two three four
four three two on${c}e
four three two one
"""
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testInsertDeleteInsertedTextAction() {
    typeTextInFile(injector.parser.parseKeys("a" + "asdf" + "<C-U>" + "<ESC>"), "on${c}e two th${c}ree")
    assertState("on${c}e two th${c}ree")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testInsertEnterAction() {
    typeTextInFile(injector.parser.parseKeys("i" + "<C-M>" + "<ESC>"), "one${c}two${c}three${c}four\n")
    assertState(
      """
    one
    ${c}two
    ${c}three
    ${c}four
    
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testInsertLineStartAction() {
    typeTextInFile(
      injector.parser.parseKeys("gI" + "four " + "<ESC>"),
      """  three t${c}wo on${c}e
${c} five six se${c}ven eight
"""
    )
    assertState(
      """
    four${c}   three two one
    four${c}  five six seven eight
    
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testInsertNewLineAboveAction() {
    typeTextInFile(
      injector.parser.parseKeys("O" + "abcde" + "<ESC>"),
      """
                ab${c}cde
                ab${c}cde
                abcde
                abc${c}de
                
                """.trimIndent()
    )
    assertState(
      """
    abcd${c}e
    abcde
    abcd${c}e
    abcde
    abcde
    abcd${c}e
    abcde
    
    """.trimIndent()
    )
  }

  @VimBehaviorDiffers(originalVimAfter = "${c}\n${c}\nabcde\n${c}\n${c}\nabcde\n")
  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testInsertNewLineAboveActionWithMultipleCaretsInLine() {
    typeTextInFile(
      injector.parser.parseKeys("O" + "<ESC>"), """
     a${c}bcd${c}e
     abc${c}d${c}e
     
     """.trimIndent()
    )
    if (VimPlugin.getOptionService()
        .isSet(OptionScope.GLOBAL, OptionConstants.experimentalapiName, OptionConstants.experimentalapiName)
    ) {
      assertState("${c}\nabcde\n${c}\nabcde\n")
    } else {
      assertState("${c}\nabcde\n${c}\nabcde\n")
    }
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testInsertNewLineBelowAction() {
    typeTextInFile(
      injector.parser.parseKeys("o" + "abcde" + "<ESC>"),
      """
                ab${c}cde
                ab${c}cde
                abcde
                abc${c}de
                
                """.trimIndent()
    )
    assertState(
      """
    abcde
    abcd${c}e
    abcde
    abcd${c}e
    abcde
    abcde
    abcd${c}e
    
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testInsertSingleCommandAction() {
    typeTextInFile(
      injector.parser.parseKeys("i" + "<C-O>" + "2h" + "<ESC>"),
      "one ${c}two ${c}three ${c}four\n"
    )
    assertState("o${c}ne t${c}wo thr${c}ee four\n")
  }

  // com.maddyhome.idea.vim.action.change.shift
  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testShiftLeftLinesAction() {
    typeTextInFile(
      injector.parser.parseKeys("2<<"),
      """        ${c}abcde
        abcde
    abcde
    ${c}abcde
    abcde
"""
    )
    assertState(
      """    ${c}abcde
    abcde
    abcde
${c}abcde
abcde
"""
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testShiftLeftMotionAction() {
    typeTextInFile(
      injector.parser.parseKeys("<j"),
      """        ${c}abcde
        abcde
    abcde
    ${c}abcde
    abcde
"""
    )
    assertState(
      """    ${c}abcde
    abcde
    abcde
${c}abcde
abcde
"""
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testShiftLeftVisualAction() {
    typeTextInFile(
      injector.parser.parseKeys("Vj<"),
      """        ${c}abcde
        abcde
    abcde
    ${c}abcde
    abcde
"""
    )
    assertState(
      """    ${c}abcde
    abcde
    abcde
${c}abcde
abcde
"""
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testShiftRightLinesAction() {
    typeTextInFile(
      injector.parser.parseKeys("2>>"),
      """    ${c}abcde
    abcde
    abcde
${c}abcde
abcde
"""
    )
    assertState(
      """        ${c}abcde
        abcde
    abcde
    ${c}abcde
    abcde
"""
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testShiftRightMotionAction() {
    typeTextInFile(
      injector.parser.parseKeys(">j"),
      """    ${c}abcde
    abcde
    abcde
${c}abcde
abcde
"""
    )
    assertState(
      """        ${c}abcde
        abcde
    abcde
    ${c}abcde
    abcde
"""
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testShiftRightVisualAction() {
    typeTextInFile(
      injector.parser.parseKeys("Vj>"),
      """    ${c}abcde
    abcde
    abcde
${c}abcde
abcde
"""
    )
    assertState(
      """        ${c}abcde
        abcde
    abcde
    ${c}abcde
    abcde
"""
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionGoToLineFirst() {
    typeTextInFile(
      injector.parser.parseKeys("i" + "<C-Home>"), """    sdfdsfa${c}dsf fg dsfg sd${c}fjgkfdgl jsdfnflgj sd
 dflgj dfdsfg
 dfsgj sdf${c}klgj"""
    )
    assertState(
      """${c}    sdfdsfadsf fg dsfg sdfjgkfdgl jsdfnflgj sd
 dflgj dfdsfg
 dfsgj sdfklgj"""
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionGotoLineLastEnd() {
    typeTextInFile(
      injector.parser.parseKeys("<C-End>"), """    sdfdsfa${c}dsf fg dsfg sd${c}fjgkfdgl jsdf
nflgj sd
 dflgj dfdsfg
 hdfsgj sdf${c}klgj
"""
    )
    assertState(
      """    sdfdsfadsf fg dsfg sdfjgkfdgl jsdf
nflgj sd
 dflgj dfdsfg
 hdfsgj sdfklgj
${c}"""
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionGotoLineLastEndInsertMode() {
    typeTextInFile(
      injector.parser.parseKeys("i" + "<C-End>"), """    sdfdsfa${c}dsf fg dsfg sd${c}fjgkfdgl jsdf
nflgj sd
 dflgj dfdsfg
 hdfsgj sdf${c}klgj
"""
    )
    assertState(
      """    sdfdsfadsf fg dsfg sdfjgkfdgl jsdf
nflgj sd
 dflgj dfdsfg
 hdfsgj sdfklgj
${c}"""
    )
  }

  @TestFor(classes = [SearchWholeWordForwardAction::class])
  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testSearchWholeWordForwardAction() {
    typeTextInFile(
      injector.parser.parseKeys("2*"),
      "q${c}we as${c}d zxc qwe asd zxc qwe asd zxc qwe asd zxc qwe asd zxc "
    )
    assertState("qwe asd zxc qwe asd zxc ${c}qwe ${c}asd zxc qwe asd zxc qwe asd zxc ")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testSearchWholeWordBackwardAction() {
    typeTextInFile(
      injector.parser.parseKeys("2#"),
      "qwe asd zxc qwe asd zxc ${c}qwe ${c}asd zxc qwe asd zxc qwe asd zxc "
    )
    assertState("${c}qwe ${c}asd zxc qwe asd zxc qwe asd zxc qwe asd zxc qwe asd zxc ")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionPercentOrMatchAction() {
    typeTextInFile(
      injector.parser.parseKeys("%"),
      "fdgkh${c}sjh thsth[ sd${c}k er{}gha re${c}ghrjae (ghoefgh kjfgh)sdgfh dgfh]"
    )
    assertState("fdgkhsjh thsth[ sdk er{${c}}gha reghrjae (ghoefgh kjfgh${c})sdgfh dgfh${c}]")
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionGotoLineLastAction() {
    typeTextInFile(
      injector.parser.parseKeys("G"), """
     dfgdfsg${c}gfdfgdfs dasgdfsk dfghsdfkj gh
     lsdjf lsj${c} flk gjdlsadlsfj ${c}lksdgfj 
     dflgjdfsgk${c}d${c}flgjdfsklg
     
     
     """.trimIndent()
    )
    assertState(
      """
    dfgdfsggfdfgdfs dasgdfsk dfghsdfkj gh
    lsdjf lsj flk gjdlsadlsfj lksdgfj 
    dflgjdfsgkdflgjdfsklg
    
    ${c}
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testMotionGotoLineLastWithArgumentAction() {
    typeTextInFile(
      injector.parser.parseKeys("1G"), """
     dfgdfsg${c}gfdfgdfs dasgdfsk dfghsdfkj gh
     lsdjf lsj${c} flk gjdlsadlsfj ${c}lksdgfj 
     dflgjdfsgk${c}d${c}flgjdfsklg
     
     
     """.trimIndent()
    )
    assertState(
      """
    ${c}dfgdfsggfdfgdfs dasgdfsk dfghsdfkj gh
    lsdjf lsj flk gjdlsadlsfj lksdgfj 
    dflgjdfsgkdflgjdfsklg
    
    
    """.trimIndent()
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testInsertAtPreviousInsert() {
    val before = """qw${c}e
  a${c}s${c}d
zx${c}c"""
    typeTextInFile(injector.parser.parseKeys("I" + "rty" + "<Esc>" + "2lj" + "gi" + "fgh" + "<Esc>"), before)
    val after = """rtyqwe
  rtyasd
rtyfg${c}hzxc"""
    assertState(after)
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testAutoIndentRange() {
    val before = "cl${c}ass C {\n C(int i) {\nmy${c}I = i;\n}\n private int myI;\n}"
    configureByJavaText(before)
    typeText(injector.parser.parseKeys("v2j="))
    val after = """${c}class C {
    C(int i) {
        myI = i;
    }
    private int myI;
}"""
    assertState(after)
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testAutoIndentMotion() {
    val before = "cl${c}ass C {\n C(int i) {\nmy${c}I = i;\n}\n private int myI;\n}"
    configureByJavaText(before)
    typeText(injector.parser.parseKeys("=3j"))
    val after = """${c}class C {
    C(int i) {
        ${c}myI = i;
    }
    private int myI;
}"""
    assertState(after)
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testAutoIndentLines() {
    val before = "class C {\n C${c}(int i) {\nmyI = i;\n}\n p${c}rivate int myI;\n}"
    configureByJavaText(before)
    typeText(injector.parser.parseKeys("=="))
    val after = "class C {\n    ${c}C(int i) {\nmyI = i;\n}\n    ${c}private int myI;\n}"
    assertState(after)
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testPutTextBeforeCursor() {
    val before = "${c}qwe asd ${c}zxc rty ${c}fgh vbn"
    configureByText(before)
    injector.registerGroup.storeText('*', "fgh", SelectionType.CHARACTER_WISE)
    typeText(injector.parser.parseKeys("\"*P" + "3l" + "\"*P"))
    val after = "fghqwfg${c}he asd fghzxfg${c}hc rty fghfgfg${c}hh vbn"
    assertState(after)
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testPutTextBeforeCursorOverlapRange() {
    val before = "${c}q${c}we asd zxc rty ${c}fgh vbn"
    val editor = configureByText(before)
    injector.registerGroup.storeText('*', "fgh")
    VimPlugin.getRegister().storeText(IjVimEditor(editor), TextRange(16, 19), SelectionType.CHARACTER_WISE, false)
    typeText(injector.parser.parseKeys("\"*P"))
    val after = "fg${c}hqfg${c}hwe asd zxc rty fg${c}hfgh vbn"
    assertState(after)
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testPutTextAfterCursor() {
    val before = "${c}qwe asd ${c}zxc rty ${c}fgh vbn"
    configureByText(before)
    injector.registerGroup.storeText('*', "fgh", SelectionType.CHARACTER_WISE)
    typeText(injector.parser.parseKeys("\"*p" + "3l" + "2\"*p"))
    val after = "qfghwe fghfg${c}hasd zfghxc fghfg${c}hrty ffghgh fghfg${c}hvbn"
    assertState(after)
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testPutTextAfterCursorOverlapRange() {
    val before = "${c}q${c}we asd zxc rty ${c}fgh vbn"
    configureByText(before)
    injector.registerGroup.storeText('*', "fgh", SelectionType.CHARACTER_WISE)
    typeText(injector.parser.parseKeys("2\"*p"))
    val after = "qfghfg${c}hwfghfg${c}he asd zxc rty ffghfg${c}hgh vbn"
    assertState(after)
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testPutTextBeforeCursorLinewise() {
    val before = """
            q${c}werty
            as${c}dfgh
            ${c}zxcvbn
            
            """.trimIndent()
    configureByText(before)
    injector.registerGroup.storeText('*', "zxcvbn\n", SelectionType.LINE_WISE)
    typeText(injector.parser.parseKeys("\"*P"))
    val after = """
            ${c}zxcvbn
            qwerty
            ${c}zxcvbn
            asdfgh
            ${c}zxcvbn
            zxcvbn
            
            """.trimIndent()
    assertState(after)
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testPutTextBeforeCursorLinewiseOverlapRange() {
    // Non-ide insert will produce double "${c}zxcvbn\n"
    testPutOverlapLine(
      """
    q${c}we${c}rty
    asdfgh
    ${c}zxcvbn
    
    """.trimIndent(),
      """
            ${c}zxcvbn
            ${c}zxcvbn
            qwerty
            asdfgh
            ${c}zxcvbn
            zxcvbn
            
            """.trimIndent(), true
    )
    testPutOverlapLine(
      """
    qwerty
    a${c}sd${c}fgh
    ${c}zxcvbn
    
    """.trimIndent(),
      """
                qwerty
                ${c}zxcvbn
                ${c}zxcvbn
                asdfgh
                ${c}zxcvbn
                zxcvbn
                
                """.trimIndent(), true
    )
    testPutOverlapLine(
      """
    qwerty
    asd${c}fgh
    ${c}zxcvb${c}n
    
    """.trimIndent(),
      """
                qwerty
                ${c}zxcvbn
                asdfgh
                ${c}zxcvbn
                ${c}zxcvbn
                zxcvbn
                
                """.trimIndent(), true
    )
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testPutTextAfterCursorLinewiseOverlapRange() {
    // Non-ide insert will produce double "${c}zxcvbn\n"
    testPutOverlapLine(
      """
    q${c}wert${c}y
    asdfgh
    ${c}zxcvbn
    
    """.trimIndent(),
      """
            qwerty
            ${c}zxcvbn
            ${c}zxcvbn
            asdfgh
            zxcvbn
            ${c}zxcvbn
            
            """.trimIndent(), false
    )
    testPutOverlapLine(
      """
    qwerty
    as${c}dfg${c}h
    ${c}zxcvbn
    
    """.trimIndent(),
      """
                qwerty
                asdfgh
                ${c}zxcvbn
                ${c}zxcvbn
                zxcvbn
                ${c}zxcvbn
                
                """.trimIndent(), false
    )
    testPutOverlapLine(
      """
    qwerty
    asdfg${c}h
    ${c}zxcv${c}bn
    
    """.trimIndent(),
      """
                qwerty
                asdfgh
                ${c}zxcvbn
                zxcvbn
                ${c}zxcvbn
                ${c}zxcvbn
                
                """.trimIndent(), false
    )
  }

  private fun testPutOverlapLine(before: String, after: String, beforeCursor: Boolean) {
    configureByText(before)
    injector.registerGroup.storeText('*', "zxcvbn\n", SelectionType.LINE_WISE)
    typeText(injector.parser.parseKeys("\"*" + if (beforeCursor) "P" else "p"))
    assertState(after)
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testPutTextAfterCursorLinewise() {
    val before = """
            q${c}werty
            as${c}dfgh
            ${c}zxcvbn
            
            """.trimIndent()
    configureByText(before)
    injector.registerGroup.storeText('*', "zxcvbn", SelectionType.LINE_WISE)
    typeText(injector.parser.parseKeys("\"*p"))
    val after = """
            qwerty
            ${c}zxcvbn
            asdfgh
            ${c}zxcvbn
            zxcvbn
            ${c}zxcvbn
            
            """.trimIndent()
    assertState(after)
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testPutTextBeforeCursorMoveCursor() {
    val before = "qw${c}e asd z${c}xc rty ${c}fgh vbn"
    configureByText(before)
    injector.registerGroup.storeText('*', "fgh", SelectionType.CHARACTER_WISE)
    typeText(injector.parser.parseKeys("l" + "\"*gP" + "b" + "\"*gP"))
    val after = "fgh${c}qwefgh asd fgh${c}zxfghc rty fgh${c}ffghgh vbn"
    assertState(after)
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testPutTextAfterCursorMoveCursor() {
    val before = "qw${c}e asd z${c}xc rty ${c}fgh vbn"
    configureByText(before)
    injector.registerGroup.storeText('*', "fgh", SelectionType.CHARACTER_WISE)
    typeText(injector.parser.parseKeys("l" + "\"*gp" + "b" + "\"*gp"))
    val after = "qwe ffgh${c}ghasd zfgh${c}xcfgh rty ffgh${c}gfghh vbn"
    assertState(after)
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testPutTextBeforeCursorMoveCursorLinewise() {
    val before = """
            qwert${c}y
            ${c}asdfgh
            zxc${c}vbn
            
            """.trimIndent()
    configureByText(before)
    injector.registerGroup.storeText('*', "zxcvbn\n", SelectionType.LINE_WISE)
    typeText(injector.parser.parseKeys("\"*gP"))
    val after = """
            zxcvbn
            ${c}qwerty
            zxcvbn
            ${c}asdfgh
            zxcvbn
            ${c}zxcvbn
            
            """.trimIndent()
    assertState(after)
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testPutTextAfterCursorMoveCursorLinewise() {
    val before = """
            qwert${c}y
            ${c}asdfgh
            zxc${c}vbn
            
            """.trimIndent()
    configureByText(before)
    injector.registerGroup.storeText('*', "zxcvbn", SelectionType.LINE_WISE)
    typeText(injector.parser.parseKeys("\"*gp"))
    val after = """
            qwerty
            zxcvbn
            ${c}asdfgh
            zxcvbn
            ${c}zxcvbn
            zxcvbn
            ${c}
            """.trimIndent()
    assertState(after)
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testPutTextBeforeCursorWithIndention() {
    val before = """class C {
    C(int i) {
        myI = i;
    }
    ${c}private int myI = 0;
    {
        ${c}private int myJ = 0;
    }
    ${c}private int myK = 0;
}"""
    configureByJavaText(before)
    injector.registerGroup.storeText('*', "private int myK = 0;\n", SelectionType.LINE_WISE)
    typeText(injector.parser.parseKeys("\"*P"))
    val after = """class C {
    C(int i) {
        myI = i;
    }
    ${c}private int myK = 0;
    private int myI = 0;
    {
        ${c}private int myK = 0;
        private int myJ = 0;
    }
    ${c}private int myK = 0;
    private int myK = 0;
}"""
    assertState(after)
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testPutTextAfterCursorWithIndention() {
    val before = """class C {
    C(int i) {
        myI = i;
    }
    ${c}private int myI = 0;
    {
        ${c}private int myJ = 0;
    }
    ${c}private int myK = 0;
}"""
    configureByJavaText(before)
    injector.registerGroup.storeText('*', "private int myK = 0;", SelectionType.LINE_WISE)
    typeText(injector.parser.parseKeys("\"*p"))
    val after = """class C {
    C(int i) {
        myI = i;
    }
    private int myI = 0;
    ${c}private int myK = 0;
    {
        private int myJ = 0;
        ${c}private int myK = 0;
    }
    private int myK = 0;
    ${c}private int myK = 0;
}"""
    assertState(after)
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testPutTextBeforeCursorBlockwise() {
    val before = """ *${c} on${c}e
 * two
"""
    configureByText(before)
    injector.registerGroup.storeText('*', " *\n *\n", SelectionType.BLOCK_WISE)
    typeText(injector.parser.parseKeys("\"*p"))
    val after = """ * ${c} *one${c} *
 *  *two *
"""
    assertState(after)
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testPutTextAfterCursorBlockwise() {
    val before = """ *${c} on${c}e
 * two
"""
    configureByText(before)
    injector.registerGroup.storeText('*', " *\n \n", SelectionType.BLOCK_WISE)
    typeText(injector.parser.parseKeys("\"*P"))
    val after = """ *${c} * on${c} *e
 *   tw  o
"""
    assertState(after)
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testPutLinewiseWithoutLineSeparatorAtTheEndOfFile() {
    val before = """
      qwe
      asd
      z${c}xc
      rty
      fg${c}h
      vb${c}n
    """.trimIndent()
    configureByText(before)
    injector.registerGroup.storeText('*', "qwe\n", SelectionType.LINE_WISE)
    typeText(injector.parser.parseKeys("\"*p"))
    val after = """
      qwe
      asd
      zxc
      ${c}qwe
      rty
      fgh
      ${c}qwe
      vbn
      ${c}qwe
      
    """.trimIndent()
    assertState(after)
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testYankMotion() {
    val before = "qwe ${c}asd ${c}zxc"
    configureByText(before)
    typeText(injector.parser.parseKeys("ye"))
    val lastRegister = VimPlugin.getRegister().lastRegister
    assertNotNull(lastRegister)
    val text = lastRegister!!.text
    assertNotNull(text)
    typeText(injector.parser.parseKeys("P"))
    val after = "qwe as${c}dasd zx${c}czxc"
    assertState(after)
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testYankMotionLineWise() {
    val before = """
            ${c}qwe
            rty
            asd
            ${c}fgh
            zxc
            vbn
            
            """.trimIndent()
    configureByText(before)
    typeText(injector.parser.parseKeys("yj"))
    val lastRegister = VimPlugin.getRegister().lastRegister
    assertNotNull(lastRegister)
    val text = lastRegister!!.text
    assertNotNull(text)
    typeText(injector.parser.parseKeys("P"))
    val after = """
            ${c}qwe
            rty
            qwe
            rty
            asd
            ${c}fgh
            zxc
            fgh
            zxc
            vbn
            
            """.trimIndent()
    assertState(after)
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun testYankLine() {
    val before = """
            ${c}qwe
            asd
            zxc
            ${c}rty
            fgh
            vbn
            
            """.trimIndent()
    configureByText(before)
    typeText(injector.parser.parseKeys("2yy"))
    val lastRegister = VimPlugin.getRegister().lastRegister
    assertNotNull(lastRegister)
    val text = lastRegister!!.text
    assertNotNull(text)
    typeText(injector.parser.parseKeys("j" + "p"))
    val after = """
            qwe
            asd
            ${c}qwe
            asd
            zxc
            rty
            fgh
            ${c}rty
            fgh
            vbn
            
            """.trimIndent()
    assertState(after)
  }

  @TestWithoutNeovim(reason = SkipNeovimReason.MULTICARET)
  fun `test multicaret with change`() {
    val before = """
            ${c}qwe
            asd
            zxc
            ${c}rty
            fgh
            vbn
            
            """.trimIndent()
    configureByText(before)
    typeText(injector.parser.parseKeys("cwblabla<Esc>p"))
    val after = """
            blablaqw${c}e
            asd
            zxc
            blablart${c}y
            fgh
            vbn
            
            """.trimIndent()
    assertState(after)
  }
}