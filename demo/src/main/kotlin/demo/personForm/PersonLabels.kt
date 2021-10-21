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

package demo.personForm

import model.ILabel

/**
 * @author Louisa Reinger
 * @author Steve Vogel
 */
enum class PersonLabels(val deutsch : String, val english : String) : ILabel {

    //Titles
    TITLE       ("Personalien",             "Personal Data"),
    PERSINFO    ("Persönliche Information", "Personal Information"),
    ADDRESS     ("Adresse",                 "Address"),
    HEADERGROUP ("Informationsübersicht",   "Information"),

    //Personal Information
    ID          ("ID",              "ID"),
    FIRSTNAME   ("Vorname",         "First name"),
    LASTNAME    ("Nachname",        "Last name"),
    GENDER      ("Geschlecht",      "Gender"),
    MARRIED     ("Verheiratet",     "Married"),
    AGE         ("Alter",           "Age"),
    SIZE        ("Grösse",          "Size"),
    OCCUPATION  ("Beruf",           "Occupation"),
    TAXNUMBER   ("Steuer-Nummer",   "Tax Number"),
    MEMBERSHIP_TYPE("Art der Mitgliedschaft", "Membership type"),

    //Adress
    POSTCODE    ("Postleitzahl",    "Postcode"),
    PLACE       ("Ort",             "Town/City"),
    STREET      ("Strasse",         "Street"),
    HOUSENUMBER ("Hausnummer",      "House Number"),


    //Additional
    ADDITIONAL  ("Zusätzliche Information", "Additional Information"),


    //Validators
    GENDERVALIDATIONMSG("Nur eine Selektion möglich", "Only 1 selection possible."),
    SIZEVALIDATIONMSG("Zu viele Nachkommastellen","Too many decimal places."),
    POSTCODEVALIDATIONMSG("Eingabe muss zwischen 3 und 5 Zeichen lang sein", "The input must be 3 - 5 characters long"),

    //SELECTIONS
    SELECTIONMAN("Mann", "Man"),
    SELECTIONWOMAN("Frau", "Woman"),
    SELECTIONOTHER("Anderes", "Other"),

    SELECTIONYES("Ja", "Yes"),
    SELECTIONNO("Nein", "No"),


    SELECTIONACTIVEMEMBER("Aktives Mitglied", "Active Member"),
    SELECTIONPASSIVMEMBER("Passiv Mitglied", "Passiv Member"),

    ;
}