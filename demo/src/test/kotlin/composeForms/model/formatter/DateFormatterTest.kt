package composeForms.model.formatter

import composeForms.model.BaseModel
import composeForms.model.Labels
import composeForms.model.attributes.StringAttribute
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateFormatterTest {

    @Test
    fun testDateFormatter(){
        //given
        var model = object: BaseModel<Labels>(title = Labels.TEST) {}
        val dateAttribute = StringAttribute(model, Labels.TEST, formatter = DateFormatter())
        val today = LocalDate.now()
        val tomorrow = LocalDate.now().plusDays(1)
        val yesterday = LocalDate.now().minusDays(1)
        val future = LocalDate.now().plusDays(123)
        val past = LocalDate.now().minusDays(123)
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

        //when
        dateAttribute.setValueAsText(today.format(formatter))
        //then
        assertEquals("Today", dateAttribute.getFormattedValue())

        //when
        dateAttribute.setValueAsText(tomorrow.format(formatter))
        //then
        assertEquals("Tomorrow", dateAttribute.getFormattedValue())

        //when
        dateAttribute.setValueAsText(yesterday.format(formatter))
        //then
        assertEquals("Yesterday", dateAttribute.getFormattedValue())


        //when
        dateAttribute.setValueAsText(future.format(formatter))
        //then
        assertEquals(future.format(formatter), dateAttribute.getFormattedValue())


        //when
        dateAttribute.setValueAsText(past.format(formatter))
        //then
        assertEquals(past.format(formatter), dateAttribute.getFormattedValue())

    }

}