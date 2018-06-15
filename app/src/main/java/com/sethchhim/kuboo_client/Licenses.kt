package com.sethchhim.kuboo_client

data class Licenses(
        val ANDROID_SPINKIT: String = getAndroidSpinKit(),
        val APACHE_V2: String = getApacheV2(),
        val BUBBLE: String = getBubble(),
        val EPUBLIB: String = getEpubLib(),
        val JUNRAR: String = getJunrar(),
        val MUPDF: String = getMuPDF(),
        val NUMBER_PROGRESS_BAR: String = getNumberProgressBar(),
        val GLIDE: String = getGlide())

fun getAndroidSpinKit(): String {
    return "The ANDROID_SPINKIT License (ANDROID_SPINKIT)\n" +
            "\n" +
            "Copyright © 2016 ybq\n" +
            "\n" +
            "Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:\n" +
            "\n" +
            "The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.\n" +
            "\n" +
            "THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE."
}

fun getBubble(): String {
    return "Copyright (c) 2015 Nazar Kanaev (nkanaev@live.com)\n" +
            "\n" +
            "Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the \"Software\"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:\n" +
            "\n" +
            "The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.\n" +
            "\n" +
            "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE."
}

fun getApacheV2(): String {
    return "\n" +
            "                                 Apache License\n" +
            "                           Version 2.0, January 2004\n" +
            "                        http://www.apache.org/licenses/\n" +
            "\n" +
            "   TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION\n" +
            "\n" +
            "   1. Definitions.\n" +
            "\n" +
            "      \"License\" shall mean the terms and conditions for use, reproduction,\n" +
            "      and distribution as defined by Sections 1 through 9 of this document.\n" +
            "\n" +
            "      \"Licensor\" shall mean the copyright owner or entity authorized by\n" +
            "      the copyright owner that is granting the License.\n" +
            "\n" +
            "      \"Legal Entity\" shall mean the union of the acting entity and all\n" +
            "      other entities that control, are controlled by, or are under common\n" +
            "      control with that entity. For the purposes of this definition,\n" +
            "      \"control\" means (i) the power, direct or indirect, to cause the\n" +
            "      direction or management of such entity, whether by contract or\n" +
            "      otherwise, or (ii) ownership of fifty percent (50%) or more of the\n" +
            "      outstanding shares, or (iii) beneficial ownership of such entity.\n" +
            "\n" +
            "      \"You\" (or \"Your\") shall mean an individual or Legal Entity\n" +
            "      exercising permissions granted by this License.\n" +
            "\n" +
            "      \"Source\" form shall mean the preferred form for making modifications,\n" +
            "      including but not limited to software source code, documentation\n" +
            "      source, and configuration files.\n" +
            "\n" +
            "      \"Object\" form shall mean any form resulting from mechanical\n" +
            "      transformation or translation of a Source form, including but\n" +
            "      not limited to compiled object code, generated documentation,\n" +
            "      and conversions to other media types.\n" +
            "\n" +
            "      \"Work\" shall mean the work of authorship, whether in Source or\n" +
            "      Object form, made available under the License, as indicated by a\n" +
            "      copyright notice that is included in or attached to the work\n" +
            "      (an example is provided in the Appendix below).\n" +
            "\n" +
            "      \"Derivative Works\" shall mean any work, whether in Source or Object\n" +
            "      form, that is based on (or derived from) the Work and for which the\n" +
            "      editorial revisions, annotations, elaborations, or other modifications\n" +
            "      represent, as a whole, an original work of authorship. For the purposes\n" +
            "      of this License, Derivative Works shall not include works that remain\n" +
            "      separable from, or merely link (or bind by name) to the interfaces of,\n" +
            "      the Work and Derivative Works thereof.\n" +
            "\n" +
            "      \"Contribution\" shall mean any work of authorship, including\n" +
            "      the original version of the Work and any modifications or additions\n" +
            "      to that Work or Derivative Works thereof, that is intentionally\n" +
            "      submitted to Licensor for inclusion in the Work by the copyright owner\n" +
            "      or by an individual or Legal Entity authorized to submit on behalf of\n" +
            "      the copyright owner. For the purposes of this definition, \"submitted\"\n" +
            "      means any form of electronic, verbal, or written communication sent\n" +
            "      to the Licensor or its representatives, including but not limited to\n" +
            "      communication on electronic mailing lists, source code control systems,\n" +
            "      and issue tracking systems that are managed by, or on behalf of, the\n" +
            "      Licensor for the purpose of discussing and improving the Work, but\n" +
            "      excluding communication that is conspicuously marked or otherwise\n" +
            "      designated in writing by the copyright owner as \"Not a Contribution.\"\n" +
            "\n" +
            "      \"Contributor\" shall mean Licensor and any individual or Legal Entity\n" +
            "      on behalf of whom a Contribution has been received by Licensor and\n" +
            "      subsequently incorporated within the Work.\n" +
            "\n" +
            "   2. Grant of Copyright License. Subject to the terms and conditions of\n" +
            "      this License, each Contributor hereby grants to You a perpetual,\n" +
            "      worldwide, non-exclusive, no-charge, royalty-free, irrevocable\n" +
            "      copyright license to reproduce, prepare Derivative Works of,\n" +
            "      publicly display, publicly perform, sublicense, and distribute the\n" +
            "      Work and such Derivative Works in Source or Object form.\n" +
            "\n" +
            "   3. Grant of Patent License. Subject to the terms and conditions of\n" +
            "      this License, each Contributor hereby grants to You a perpetual,\n" +
            "      worldwide, non-exclusive, no-charge, royalty-free, irrevocable\n" +
            "      (except as stated in this section) patent license to make, have made,\n" +
            "      use, offer to sell, sell, import, and otherwise transfer the Work,\n" +
            "      where such license applies only to those patent claims licensable\n" +
            "      by such Contributor that are necessarily infringed by their\n" +
            "      Contribution(s) alone or by combination of their Contribution(s)\n" +
            "      with the Work to which such Contribution(s) was submitted. If You\n" +
            "      institute patent litigation against any entity (including a\n" +
            "      cross-claim or counterclaim in a lawsuit) alleging that the Work\n" +
            "      or a Contribution incorporated within the Work constitutes direct\n" +
            "      or contributory patent infringement, then any patent licenses\n" +
            "      granted to You under this License for that Work shall terminate\n" +
            "      as of the date such litigation is filed.\n" +
            "\n" +
            "   4. Redistribution. You may reproduce and distribute copies of the\n" +
            "      Work or Derivative Works thereof in any medium, with or without\n" +
            "      modifications, and in Source or Object form, provided that You\n" +
            "      meet the following conditions:\n" +
            "\n" +
            "      (a) You must give any other recipients of the Work or\n" +
            "          Derivative Works a copy of this License; and\n" +
            "\n" +
            "      (b) You must cause any modified files to carry prominent notices\n" +
            "          stating that You changed the files; and\n" +
            "\n" +
            "      (c) You must retain, in the Source form of any Derivative Works\n" +
            "          that You distribute, all copyright, patent, trademark, and\n" +
            "          attribution notices from the Source form of the Work,\n" +
            "          excluding those notices that do not pertain to any part of\n" +
            "          the Derivative Works; and\n" +
            "\n" +
            "      (d) If the Work includes a \"NOTICE\" text file as part of its\n" +
            "          distribution, then any Derivative Works that You distribute must\n" +
            "          include a readable copy of the attribution notices contained\n" +
            "          within such NOTICE file, excluding those notices that do not\n" +
            "          pertain to any part of the Derivative Works, in at least one\n" +
            "          of the following places: within a NOTICE text file distributed\n" +
            "          as part of the Derivative Works; within the Source form or\n" +
            "          documentation, if provided along with the Derivative Works; or,\n" +
            "          within a display generated by the Derivative Works, if and\n" +
            "          wherever such third-party notices normally appear. The contents\n" +
            "          of the NOTICE file are for informational purposes only and\n" +
            "          do not modify the License. You may add Your own attribution\n" +
            "          notices within Derivative Works that You distribute, alongside\n" +
            "          or as an addendum to the NOTICE text from the Work, provided\n" +
            "          that such additional attribution notices cannot be construed\n" +
            "          as modifying the License.\n" +
            "\n" +
            "      You may add Your own copyright statement to Your modifications and\n" +
            "      may provide additional or different license terms and conditions\n" +
            "      for use, reproduction, or distribution of Your modifications, or\n" +
            "      for any such Derivative Works as a whole, provided Your use,\n" +
            "      reproduction, and distribution of the Work otherwise complies with\n" +
            "      the conditions stated in this License.\n" +
            "\n" +
            "   5. Submission of Contributions. Unless You explicitly state otherwise,\n" +
            "      any Contribution intentionally submitted for inclusion in the Work\n" +
            "      by You to the Licensor shall be under the terms and conditions of\n" +
            "      this License, without any additional terms or conditions.\n" +
            "      Notwithstanding the above, nothing herein shall supersede or modify\n" +
            "      the terms of any separate license agreement you may have executed\n" +
            "      with Licensor regarding such Contributions.\n" +
            "\n" +
            "   6. Trademarks. This License does not grant permission to use the trade\n" +
            "      names, trademarks, service marks, or product names of the Licensor,\n" +
            "      except as required for reasonable and customary use in describing the\n" +
            "      origin of the Work and reproducing the content of the NOTICE file.\n" +
            "\n" +
            "   7. Disclaimer of Warranty. Unless required by applicable law or\n" +
            "      agreed to in writing, Licensor provides the Work (and each\n" +
            "      Contributor provides its Contributions) on an \"AS IS\" BASIS,\n" +
            "      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or\n" +
            "      implied, including, without limitation, any warranties or conditions\n" +
            "      of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A\n" +
            "      PARTICULAR PURPOSE. You are solely responsible for determining the\n" +
            "      appropriateness of using or redistributing the Work and assume any\n" +
            "      risks associated with Your exercise of permissions under this License.\n" +
            "\n" +
            "   8. Limitation of Liability. In no event and under no legal theory,\n" +
            "      whether in tort (including negligence), contract, or otherwise,\n" +
            "      unless required by applicable law (such as deliberate and grossly\n" +
            "      negligent acts) or agreed to in writing, shall any Contributor be\n" +
            "      liable to You for damages, including any direct, indirect, special,\n" +
            "      incidental, or consequential damages of any character arising as a\n" +
            "      result of this License or out of the use or inability to use the\n" +
            "      Work (including but not limited to damages for loss of goodwill,\n" +
            "      work stoppage, computer failure or malfunction, or any and all\n" +
            "      other commercial damages or losses), even if such Contributor\n" +
            "      has been advised of the possibility of such damages.\n" +
            "\n" +
            "   9. Accepting Warranty or Additional Liability. While redistributing\n" +
            "      the Work or Derivative Works thereof, You may choose to offer,\n" +
            "      and charge a fee for, acceptance of support, warranty, indemnity,\n" +
            "      or other liability obligations and/or rights consistent with this\n" +
            "      License. However, in accepting such obligations, You may act only\n" +
            "      on Your own behalf and on Your sole responsibility, not on behalf\n" +
            "      of any other Contributor, and only if You agree to indemnify,\n" +
            "      defend, and hold each Contributor harmless for any liability\n" +
            "      incurred by, or claims asserted against, such Contributor by reason\n" +
            "      of your accepting any such warranty or additional liability.\n" +
            "\n" +
            "   END OF TERMS AND CONDITIONS\n" +
            "\n" +
            "   APPENDIX: How to apply the Apache License to your work.\n" +
            "\n" +
            "      To apply the Apache License to your work, attach the following\n" +
            "      boilerplate notice, with the fields enclosed by brackets \"[]\"\n" +
            "      replaced with your own identifying information. (Don't include\n" +
            "      the brackets!)  The text should be enclosed in the appropriate\n" +
            "      comment syntax for the file format. We also recommend that a\n" +
            "      file or class name and description of purpose be included on the\n" +
            "      same \"printed page\" as the copyright notice for easier\n" +
            "      identification within third-party archives.\n" +
            "\n" +
            "   Copyright [yyyy] [name of copyright owner]\n" +
            "\n" +
            "   Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
            "   you may not use this file except in compliance with the License.\n" +
            "   You may obtain a copy of the License at\n" +
            "\n" +
            "       http://www.apache.org/licenses/LICENSE-2.0\n" +
            "\n" +
            "   Unless required by applicable law or agreed to in writing, software\n" +
            "   distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
            "   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
            "   See the License for the specific language governing permissions and\n" +
            "   limitations under the License."
}

fun getEpubLib(): String {
    return "GNU LESSER GENERAL PUBLIC LICENSE\n" +
            "Version 3, 29 June 2007\n" +
            "\n" +
            "Copyright © 2007 Free Software Foundation, Inc. <https://fsf.org/>\n" +
            "\n" +
            "Everyone is permitted to copy and distribute verbatim copies of this license document, but changing it is not allowed.\n" +
            "\n" +
            "This version of the GNU Lesser General Public License incorporates the terms and conditions of version 3 of the GNU General Public License, supplemented by the additional permissions listed below.\n" +
            "\n" +
            "0. Additional Definitions.\n" +
            "As used herein, “this License” refers to version 3 of the GNU Lesser General Public License, and the “GNU GPL” refers to version 3 of the GNU General Public License.\n" +
            "\n" +
            "“The Library” refers to a covered work governed by this License, other than an Application or a Combined Work as defined below.\n" +
            "\n" +
            "An “Application” is any work that makes use of an interface provided by the Library, but which is not otherwise based on the Library. Defining a subclass of a class defined by the Library is deemed a mode of using an interface provided by the Library.\n" +
            "\n" +
            "A “Combined Work” is a work produced by combining or linking an Application with the Library. The particular version of the Library with which the Combined Work was made is also called the “Linked Version”.\n" +
            "\n" +
            "The “Minimal Corresponding Source” for a Combined Work means the Corresponding Source for the Combined Work, excluding any source code for portions of the Combined Work that, considered in isolation, are based on the Application, and not on the Linked Version.\n" +
            "\n" +
            "The “Corresponding Application Code” for a Combined Work means the object code and/or source code for the Application, including any data and utility programs needed for reproducing the Combined Work from the Application, but excluding the System Libraries of the Combined Work.\n" +
            "\n" +
            "1. Exception to Section 3 of the GNU GPL.\n" +
            "You may convey a covered work under sections 3 and 4 of this License without being bound by section 3 of the GNU GPL.\n" +
            "\n" +
            "2. Conveying Modified Versions.\n" +
            "If you modify a copy of the Library, and, in your modifications, a facility refers to a function or data to be supplied by an Application that uses the facility (other than as an argument passed when the facility is invoked), then you may convey a copy of the modified version:\n" +
            "\n" +
            "a) under this License, provided that you make a good faith effort to ensure that, in the event an Application does not supply the function or data, the facility still operates, and performs whatever part of its purpose remains meaningful, or\n" +
            "b) under the GNU GPL, with none of the additional permissions of this License applicable to that copy.\n" +
            "3. Object Code Incorporating Material from Library Header Files.\n" +
            "The object code form of an Application may incorporate material from a header file that is part of the Library. You may convey such object code under terms of your choice, provided that, if the incorporated material is not limited to numerical parameters, data structure layouts and accessors, or small macros, inline functions and templates (ten or fewer lines in length), you do both of the following:\n" +
            "\n" +
            "a) Give prominent notice with each copy of the object code that the Library is used in it and that the Library and its use are covered by this License.\n" +
            "b) Accompany the object code with a copy of the GNU GPL and this license document.\n" +
            "4. Combined Works.\n" +
            "You may convey a Combined Work under terms of your choice that, taken together, effectively do not restrict modification of the portions of the Library contained in the Combined Work and reverse engineering for debugging such modifications, if you also do each of the following:\n" +
            "\n" +
            "a) Give prominent notice with each copy of the Combined Work that the Library is used in it and that the Library and its use are covered by this License.\n" +
            "b) Accompany the Combined Work with a copy of the GNU GPL and this license document.\n" +
            "c) For a Combined Work that displays copyright notices during execution, include the copyright notice for the Library among these notices, as well as a reference directing the user to the copies of the GNU GPL and this license document.\n" +
            "d) Do one of the following:\n" +
            "0) Convey the Minimal Corresponding Source under the terms of this License, and the Corresponding Application Code in a form suitable for, and under terms that permit, the user to recombine or relink the Application with a modified version of the Linked Version to produce a modified Combined Work, in the manner specified by section 6 of the GNU GPL for conveying Corresponding Source.\n" +
            "1) Use a suitable shared library mechanism for linking with the Library. A suitable mechanism is one that (a) uses at run time a copy of the Library already present on the user's computer system, and (b) will operate properly with a modified version of the Library that is interface-compatible with the Linked Version.\n" +
            "e) Provide Installation Information, but only if you would otherwise be required to provide such information under section 6 of the GNU GPL, and only to the extent that such information is necessary to install and execute a modified version of the Combined Work produced by recombining or relinking the Application with a modified version of the Linked Version. (If you use option 4d0, the Installation Information must accompany the Minimal Corresponding Source and Corresponding Application Code. If you use option 4d1, you must provide the Installation Information in the manner specified by section 6 of the GNU GPL for conveying Corresponding Source.)\n" +
            "5. Combined Libraries.\n" +
            "You may place library facilities that are a work based on the Library side by side in a single library together with other library facilities that are not Applications and are not covered by this License, and convey such a combined library under terms of your choice, if you do both of the following:\n" +
            "\n" +
            "a) Accompany the combined library with a copy of the same work based on the Library, uncombined with any other library facilities, conveyed under the terms of this License.\n" +
            "b) Give prominent notice with the combined library that part of it is a work based on the Library, and explaining where to find the accompanying uncombined form of the same work.\n" +
            "6. Revised Versions of the GNU Lesser General Public License.\n" +
            "The Free Software Foundation may publish revised and/or new versions of the GNU Lesser General Public License from time to time. Such new versions will be similar in spirit to the present version, but may differ in detail to address new problems or concerns.\n" +
            "\n" +
            "Each version is given a distinguishing version number. If the Library as you received it specifies that a certain numbered version of the GNU Lesser General Public License “or any later version” applies to it, you have the option of following the terms and conditions either of that published version or of any later version published by the Free Software Foundation. If the Library as you received it does not specify a version number of the GNU Lesser General Public License, you may choose any version of the GNU Lesser General Public License ever published by the Free Software Foundation.\n" +
            "\n" +
            "If the Library as you received it specifies that a proxy can decide whether future versions of the GNU Lesser General Public License shall apply, that proxy's public statement of acceptance of any version is permanent authorization for you to choose that version for the Library."
}

fun getJunrar(): String {
    return " ******    *****   ******   UnRAR - free utility for RAR archives\n" +
            " **   **  **   **  **   **  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
            " ******   *******  ******    License for use and distribution of\n" +
            " **   **  **   **  **   **   ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
            " **   **  **   **  **   **         FREE portable version\n" +
            "                                   ~~~~~~~~~~~~~~~~~~~~~\n" +
            "\n" +
            "      The source code of UnRAR utility is freeware. This means:\n" +
            "\n" +
            "   1. All copyrights to RAR and the utility UnRAR are exclusively\n" +
            "      owned by the author - Alexander Roshal.\n" +
            "\n" +
            "   2. The UnRAR sources may be used in any software to handle RAR\n" +
            "      archives without limitations free of charge, but cannot be used\n" +
            "      to re-create the RAR compression algorithm, which is proprietary.\n" +
            "      Distribution of modified UnRAR sources in separate form or as a\n" +
            "      part of other software is permitted, provided that it is clearly\n" +
            "      stated in the documentation and source comments that the code may\n" +
            "      not be used to develop a RAR (WinRAR) compatible archiver.\n" +
            "\n" +
            "   3. The UnRAR utility may be freely distributed. It is allowed\n" +
            "      to distribute UnRAR inside of other software packages.\n" +
            "\n" +
            "   4. THE RAR ARCHIVER AND THE UnRAR UTILITY ARE DISTRIBUTED \"AS IS\".\n" +
            "      NO WARRANTY OF ANY KIND IS EXPRESSED OR IMPLIED.  YOU USE AT \n" +
            "      YOUR OWN RISK. THE AUTHOR WILL NOT BE LIABLE FOR DATA LOSS, \n" +
            "      DAMAGES, LOSS OF PROFITS OR ANY OTHER KIND OF LOSS WHILE USING\n" +
            "      OR MISUSING THIS SOFTWARE.\n" +
            "\n" +
            "   5. Installing and using the UnRAR utility signifies acceptance of\n" +
            "      these terms and conditions of the license.\n" +
            "\n" +
            "   6. If you don't agree with terms of the license you must remove\n" +
            "      UnRAR files from your storage devices and cease to use the\n" +
            "      utility.\n" +
            "\n" +
            "      Thank you for your interest in RAR and UnRAR.\n" +
            "\n" +
            "\n" +
            "                                            Alexander L. Roshal"
}

fun getMuPDF(): String {
    return "MuPDF is Copyright (c) 2006-2017 Artifex Software, Inc.\n" +
            "\n" +
            "This program is free software: you can redistribute it and/or modify it under\n" +
            "the terms of the GNU Affero General Public License as published by the Free\n" +
            "Software Foundation, either version 3 of the License, or (at your option) any\n" +
            "later version.\n" +
            "\n" +
            "This program is distributed in the hope that it will be useful, but WITHOUT ANY\n" +
            "WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A\n" +
            "PARTICULAR PURPOSE. See the GNU General Public License for more details.\n" +
            "\n" +
            "You should have received a copy of the GNU Affero General Public License along\n" +
            "with this program. If not, see <http://www.gnu.org/licenses/>.\n" +
            "\n" +
            "For commercial licensing, including our \"Indie Dev\" friendly options,\n" +
            "please contact sales@artifex.com."
}

fun getNumberProgressBar(): String {
    return "The MIT License (MIT)\n" +
            "\n" +
            "Copyright (c) 2014 Daimajia\n" +
            "\n" +
            "Permission is hereby granted, free of charge, to any person obtaining a copy\n" +
            "of this software and associated documentation files (the \"Software\"), to deal\n" +
            "in the Software without restriction, including without limitation the rights\n" +
            "to use, copy, modify, merge, publish, distribute, sublicense, and/or sell\n" +
            "copies of the Software, and to permit persons to whom the Software is\n" +
            "furnished to do so, subject to the following conditions:\n" +
            "\n" +
            "The above copyright notice and this permission notice shall be included in all\n" +
            "copies or substantial portions of the Software.\n" +
            "\n" +
            "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\n" +
            "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\n" +
            "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE\n" +
            "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\n" +
            "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\n" +
            "OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE\n" +
            "SOFTWARE."
}

fun getGlide(): String {
    return "" +
            "License for everything not in third_party and not otherwise marked:\n" +
            "\n" +
            "Copyright 2014 Google, Inc. All rights reserved.\n" +
            "\n" +
            "Redistribution and use in source and binary forms, with or without modification, are\n" +
            "permitted provided that the following conditions are met:\n" +
            "\n" +
            "   1. Redistributions of source code must retain the above copyright notice, this list of\n" +
            "         conditions and the following disclaimer.\n" +
            "\n" +
            "   2. Redistributions in binary form must reproduce the above copyright notice, this list\n" +
            "         of conditions and the following disclaimer in the documentation and/or other materials\n" +
            "         provided with the distribution.\n" +
            "\n" +
            "THIS SOFTWARE IS PROVIDED BY GOOGLE, INC. ``AS IS'' AND ANY EXPRESS OR IMPLIED\n" +
            "WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND\n" +
            "FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL GOOGLE, INC. OR\n" +
            "CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR\n" +
            "CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR\n" +
            "SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON\n" +
            "ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING\n" +
            "NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF\n" +
            "ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\n" +
            "\n" +
            "The views and conclusions contained in the software and documentation are those of the\n" +
            "authors and should not be interpreted as representing official policies, either expressed\n" +
            "or implied, of Google, Inc.\n" +
            "---------------------------------------------------------------------------------------------\n" +
            "License for third_party/disklrucache:\n" +
            "\n" +
            "Copyright 2012 Jake Wharton\n" +
            "Copyright 2011 The Android Open Source Project\n" +
            "\n" +
            "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
            "you may not use this file except in compliance with the License.\n" +
            "You may obtain a copy of the License at\n" +
            "\n" +
            "   http://www.apache.org/licenses/LICENSE-2.0\n" +
            "\n" +
            "Unless required by applicable law or agreed to in writing, software\n" +
            "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
            "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
            "See the License for the specific language governing permissions and\n" +
            "limitations under the License.\n" +
            "---------------------------------------------------------------------------------------------\n" +
            "License for third_party/gif_decoder:\n" +
            "\n" +
            "Copyright (c) 2013 Xcellent Creations, Inc.\n" +
            "\n" +
            "Permission is hereby granted, free of charge, to any person obtaining\n" +
            "a copy of this software and associated documentation files (the\n" +
            "\"Software\"), to deal in the Software without restriction, including\n" +
            "without limitation the rights to use, copy, modify, merge, publish,\n" +
            "distribute, sublicense, and/or sell copies of the Software, and to\n" +
            "permit persons to whom the Software is furnished to do so, subject to\n" +
            "the following conditions:\n" +
            "\n" +
            "The above copyright notice and this permission notice shall be\n" +
            "included in all copies or substantial portions of the Software.\n" +
            "\n" +
            "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND,\n" +
            "EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF\n" +
            "MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND\n" +
            "NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE\n" +
            "LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION\n" +
            "OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION\n" +
            "WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.\n" +
            "---------------------------------------------------------------------------------------------\n" +
            "License for third_party/gif_encoder/AnimatedGifEncoder.java and\n" +
            "third_party/gif_encoder/LZWEncoder.java:\n" +
            "\n" +
            "No copyright asserted on the source code of this class. May be used for any\n" +
            "purpose, however, refer to the Unisys LZW patent for restrictions on use of\n" +
            "the associated LZWEncoder class. Please forward any corrections to\n" +
            "kweiner@fmsware.com.\n" +
            "\n" +
            "-----------------------------------------------------------------------------\n" +
            "License for third_party/gif_encoder/NeuQuant.java\n" +
            "\n" +
            "Copyright (c) 1994 Anthony Dekker\n" +
            "\n" +
            "NEUQUANT Neural-Net quantization algorithm by Anthony Dekker, 1994. See\n" +
            "\"Kohonen neural networks for optimal colour quantization\" in \"Network:\n" +
            "Computation in Neural Systems\" Vol. 5 (1994) pp 351-367. for a discussion of\n" +
            "the algorithm.\n" +
            "\n" +
            "Any party obtaining a copy of these files from the author, directly or\n" +
            "indirectly, is granted, free of charge, a full and unrestricted irrevocable,\n" +
            "world-wide, paid up, royalty-free, nonexclusive right and license to deal in\n" +
            "this software and documentation files (the \"Software\"), including without\n" +
            "limitation the rights to use, copy, modify, merge, publish, distribute,\n" +
            "sublicense, and/or sell copies of the Software, and to permit persons who\n" +
            "receive copies from any such party to do so, with the only requirement being\n" +
            "that this copyright notice remain intact."
}