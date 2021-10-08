/*
 *
 *   ========================LICENSE_START=================================
 *   Compose Forms
 *   %%
 *   Copyright (C) 2021 FHNW Technik
 *   %%
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *   =========================LICENSE_END==================================
 *
 */

package model

/**
 * @author Louisa Reinger
 * @author Steve Vogel
 */
enum class Labels(val test: String, val eng: String): ILabel {
    TEST("test", "testEng"),
    MIN4CHARS("Der Wert muss mindestens 4 Buchstaben haben.","Der Wert muss mindestens 4 Buchstaben haben."),
    MAX4CHARS("Der Wert darf maximal 4 Buchstaben haben.","Der Wert darf maximal 4 Buchstaben haben."),

    WRONGINPUT("Falsche Eingabe getätigt","Falsche Eingabe getätigt"),

    EMTPY("", ""),

    CUSTOMVALIDATORMSG("Length must be at least 4","Length must be at least 4"),
    CUSTOMVALIDATORMSG1("Changed msg", "Changed msg"),
    OWNMSG("Own message","Own message"),


    FALSETEXT("No", "No"),
    TRUETEXT("Yes", "Yes"),

    DECISIONTEXT1("val1", "val1"),
    DECISIONTEXT2("val2","val2"),

    DEC1("Dec1","Dec1"),
    DEC2("Dec2", "Dec2"),

    ELEMENT1("Element1", "Element1"),
    ELEMENT2("Element2", "Element2"),
    ELEMENT3("Element3", "Element3"),
    ELEMENT4("Element4", "Element4"),

    A("A", "A"),
    B("B", "B"),
    C("C", "C"),
    D("D", "D"),
    E("E", "E"),


    HALLO("Hallo", "Hallo"),

    ENTRY1("Entry1", "Entry1"),
    ENTRY2("Entry2", "Entry2"),
    ENTRY3("Entry3", "Entry3"),




}