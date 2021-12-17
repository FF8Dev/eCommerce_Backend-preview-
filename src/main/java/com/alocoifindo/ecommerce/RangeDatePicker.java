/*
 * Copyright Alocoifindo 2021®
 * GitHub with ♥︎ for educational purposes
 * https://alocosite.w3spaces.com
 */
package com.alocoifindo.ecommerce;

import static com.alocoifindo.ecommerce.ApplicationUI.appUI;
import com.github.lgooddatepicker.components.CalendarPanel;
import com.github.lgooddatepicker.components.ComponentEvent;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.optionalusertools.DateVetoPolicy;
import com.github.lgooddatepicker.optionalusertools.PickerUtilities;
import com.github.lgooddatepicker.zinternaltools.Convert;
import com.github.lgooddatepicker.zinternaltools.CustomPopup;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import com.github.lgooddatepicker.zinternaltools.InternalUtilities;
import com.privatejgoodies.forms.factories.CC;
import com.privatejgoodies.forms.layout.FormLayout;
import java.awt.Color;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author facundoferreyra
 */
public class RangeDatePicker extends DatePicker {

    /**
     * calendarPanel, This holds the calendar panel GUI component of this date
     * picker. This should be null when the date picker calendar is closed, and
     * hold a calendar panel instance when the date picker calendar is opened.
     */
    public CalendarPanel calendarPanel = null;

    /**
     * popup, This is the custom popup instance for this date picker. This
     * should remain null until a popup is opened. Creating a custom popup class
     * allowed us to control the details of when the popup menu should be open
     * or closed.
     */
    private CustomPopup popup = null;

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JTextField dateTextField;
    public JToggleButton toggleCalendarButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
    /**
     * convert, This utility class instance is used to get or set the DatePicker
     * java.time.LocalDate value, using other common date related data types.
     */
    private Convert convert;

    /**
     * settings, This holds the settings instance for this date picker. Default
     * settings are generated automatically. Custom settings may optionally be
     * supplied in the DatePicker constructor.
     *
     * This will never be null after it is initialized, but it could be null
     * before setSettings() is called for the first time. Any functions that
     * rely on the settings should check for null and return if null, before
     * continuing the function.
     */
    private DatePickerSettings settings;

    /**
     * skipTextFieldChangedFunctionWhileTrue, While this is true, the function
     * "zTextFieldChangedSoIndicateIfValidAndStoreWhenValid()" will not be
     * executed in response to date text field text change events.
     */
    private boolean skipTextFieldChangedFunctionWhileTrue = false;

    /**
     * lastValidDate, This holds the last valid date that was entered into the
     * date picker. This value is returned from the function
     * DatePicker.getDate();
     *
     * Implementation note: After initialization, variable should never be -set-
     * directly. Instead, use the date setting function that will notify the
     * list of dateChangeListeners each time that this value is changed.
     */
    private LocalDate lastValidDate = null;

    /**
     * dateChangeListeners, This holds a list of date change listeners that wish
     * to be notified each time that the last valid date is changed.
     */
    private ArrayList<DateChangeListener> dateChangeListeners = new ArrayList<DateChangeListener>();

    /**
     * lastPopupCloseTime, This holds a timestamp that indicates when the
     * calendar was last closed. This is used to implement a workaround for
     * event behavior that was causing the date picker class to erroneously
     * re-open a calendar when the user was clicking on the show calendar button
     * in an attempt to close the previous calendar.
     */
    private Instant lastPopupCloseTime = Instant.now();

    /**
     * convert, This is used to access the Convert class instance. The convert
     * class allows the programmer to get or set the date picker
     * java.time.LocalDate value using other common data types, such as
     * java.util.Date. Example usage:
     * datePicker.convert().getDateWithDefaultZone(); See the documentation of
     * the Convert class for additional information and usage examples.
     */
    public Convert convert() {
        return convert;
    }

    /**
     * Constructor with Custom Settings, Create a date picker instance using the
     * supplied date picker settings.
     */
    public RangeDatePicker(DatePickerSettings settings) {

        initComponents();
        this.convert = new Convert(this);
            // Shrink the toggle calendar button to a reasonable size.
            toggleCalendarButton.setMargin(new java.awt.Insets(1, 2, 1, 2));
            // Add a change listener to the text field.
            zAddTextChangeListener();

//            addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    System.out.println("Button clicked");
//                }
//            });
        // Save and apply the supplied settings.
        setSettings(settings);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dateTextField = new JTextField();
        toggleCalendarButton = new JToggleButton();

            //======== this ========
            setLayout(new FormLayout(
                    "pref:grow, [3px,pref], [26px,pref]",
                    "fill:pref:grow"));
            //---- dateTextField ----
            dateTextField.setMargin(new Insets(1, 3, 2, 2));
            dateTextField.setBorder(new CompoundBorder(
                    new MatteBorder(1, 1, 1, 1, new Color(122, 138, 153)),
                    new EmptyBorder(1, 3, 2, 2)));
            dateTextField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    setTextFieldToValidStateIfNeeded();
                }
            });
            add(dateTextField, CC.xy(1, 1));
        //---- toggleCalendarButton ----
        toggleCalendarButton.setIcon(new ImageIcon(getClass().getResource("/calendar-20.png")));
        toggleCalendarButton.setFocusPainted(false);
        toggleCalendarButton.setFocusable(false);
        toggleCalendarButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("RangeDatePicker detected mousePressed");
                SwingUtilities.getWindowAncestor(appUI);
                zEventToggleCalendarButtonMousePressed(e);
            }
        });
//            add(toggleCalendarButton, CC.xy(3, 1));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    
    public void togglePopup() {
        // If a popup calendar was closed in the last 200 milliseconds, then do not open a new one.
        // This is a workaround for a problem where the toggle calendar button would erroneously
        // reopen a calendar after closing one.
        if ((Instant.now().toEpochMilli() - lastPopupCloseTime.toEpochMilli()) < 200) {
            return;
        }
        openPopup();
    }

    public void openPopup() {
        if (isPopupOpen()) {
            closePopup();
            return;
        }

        if (settings == null) {
            return;
        }
        // If the component is disabled, do nothing.
        if (!isEnabled()) {
            return;
        }
        // If this function was called programmatically, we may need to change the focus to this
        // popup.
        if (!dateTextField.hasFocus()) {
            dateTextField.requestFocusInWindow();
        }
        // Get the last valid date, to pass to the calendar if needed.
        LocalDate selectedDateForCalendar = lastValidDate;
        // Create a new calendar panel. 
        // Use the CalendarPanel constructor that is made for the DatePicker class
        DatePicker thisDatePicker = this;
        calendarPanel = new CalendarPanel(thisDatePicker);

        fireComponentEvent(new ComponentEvent(ComponentEvent.PREVIOUS_YEAR, calendarPanel.getPreviousYearButton()));
        fireComponentEvent(new ComponentEvent(ComponentEvent.PREVIOUS_MONTH, calendarPanel.getPreviousMonthButton()));
        fireComponentEvent(new ComponentEvent(ComponentEvent.NEXT_MONTH, calendarPanel.getNextMonthButton()));
        fireComponentEvent(new ComponentEvent(ComponentEvent.NEXT_YEAR, calendarPanel.getNextYearButton()));

        // If needed, apply the selected date to the calendar.
        if (selectedDateForCalendar != null) {
            calendarPanel.setSelectedDate(selectedDateForCalendar);
        }
        // Create a new custom popup.
        popup = new CustomPopup(calendarPanel, SwingUtilities.getWindowAncestor(this),
                this, settings.getBorderCalendarPopup());
        // Calculate the default origin for the popup.
        int defaultX = toggleCalendarButton.getLocationOnScreen().x
                + toggleCalendarButton.getBounds().width - popup.getBounds().width - 2;
        int defaultY = toggleCalendarButton.getLocationOnScreen().y
                + toggleCalendarButton.getBounds().height + 2;
        // Determine which component to use as the vertical flip reference component.
        JComponent verticalFlipReference = (settings.getVisibleDateTextField())
                ? dateTextField : toggleCalendarButton;
        // Set the popup location.
        zSetPopupLocation(popup, defaultX, defaultY, this, verticalFlipReference, 2, 6);
        // Show the popup and focus the calendar.
        popup.show();
        calendarPanel.requestFocus();
    }

        /**
         * zAddTextChangeListener, This add a text change listener to the date
         * text field, so that we can respond to text as it is typed.
         */
        private void zAddTextChangeListener() {
            dateTextField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    zEventTextFieldChanged();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    zEventTextFieldChanged();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    zEventTextFieldChanged();
                }
            });
        }
    /**
     * zEventToggleCalendarButtonMousePressed, This is called when the user
     * clicks on the "toggle calendar" button of the date picker.
     *
     * This will create a calendar panel and a popup, and display them to the
     * user. If a calendar panel is already opened, it will be closed instead.
     */
    private void zEventToggleCalendarButtonMousePressed(MouseEvent event) {
        togglePopup();
    }

        /**
         * zEventTextFieldChanged, This is called whenever the text in the date
         * picker text field has changed, whether programmatically or by the
         * user.
         *
         * If the current text contains a valid date, it will be stored in the
         * variable lastValidDate. Otherwise, the lastValidDate will not be
         * changed.
         *
         * This will also call the function to indicate to the user if the
         * currently text is a valid date, invalid text, or a vetoed date. These
         * indications are created by using font, color, and background changes
         * of the text field.
         */
        private void zEventTextFieldChanged() {
            if (settings == null) {
                return;
            }
            // Skip this function if it should not be run.
            if (skipTextFieldChangedFunctionWhileTrue) {
                return;
            }
            // Gather some variables that we will need.
            String dateText = dateTextField.getText();
            boolean textIsEmpty = dateText.trim().isEmpty();
            DateVetoPolicy vetoPolicy = settings.getVetoPolicy();
            boolean nullIsAllowed = settings.getAllowEmptyDates();
            // If the text is not empty, then try to parse the date.
            LocalDate parsedDate = null;
            if (!textIsEmpty) {
                parsedDate = InternalUtilities.getParsedDateOrNull(dateText,
                        settings.getFormatForDatesCommonEra(),
                        settings.getFormatForDatesBeforeCommonEra(),
                        settings.getFormatsForParsing());
            }
            // If the date was parsed successfully, then check it against the veto policy.
            boolean dateIsVetoed = false;
            if (parsedDate != null) {
                dateIsVetoed = InternalUtilities.isDateVetoed(vetoPolicy, parsedDate);
            }
            // If the date is a valid empty date, then set the last valid date to null.
            if (textIsEmpty && nullIsAllowed) {
                zInternalSetLastValidDateAndNotifyListeners(null);
            }
            // If the date is a valid parsed date, then store the last valid date.
            if ((!textIsEmpty) && (parsedDate != null) && (dateIsVetoed == false)) {
                zInternalSetLastValidDateAndNotifyListeners(parsedDate);
            }
            // Draw the date status indications for the user.
            zDrawTextFieldIndicators();
            // Fire a change event for beans binding.
            firePropertyChange("text", null, dateTextField.getText());
        }
    /**
     * zInternalSetLastValidDateAndNotifyListeners, This should be called
     * whenever we need to change the last valid date variable. This will store
     * the supplied last valid date. If needed, this will notify all date change
     * listeners that the date has been changed. This does -not- update the
     * displayed calendar, and does not perform any other tasks besides those
     * described here.
     */
    private void zInternalSetLastValidDateAndNotifyListeners(LocalDate newDate) {
        LocalDate oldDate = lastValidDate;
        lastValidDate = newDate;
        if (!PickerUtilities.isSameLocalDate(oldDate, newDate)) {
            for (DateChangeListener dateChangeListener : dateChangeListeners) {
                DateChangeEvent dateChangeEvent = new DateChangeEvent(this, oldDate, newDate);
                dateChangeListener.dateChanged(dateChangeEvent);
            }
            // Fire a change event for beans binding.
            firePropertyChange("date", oldDate, newDate);
        }
    }

    /**
     * zSetPopupLocation, This calculates and sets the appropriate location for
     * the popup windows, for both the DatePicker and the TimePicker.
     */
    static void zSetPopupLocation(CustomPopup popup, int defaultX, int defaultY, JComponent picker,
            JComponent verticalFlipReference, int verticalFlipDistance, int bottomOverlapAllowed) {
        // Gather some variables that we will need.
        Window topWindowOrNull = SwingUtilities.getWindowAncestor(picker);
        Rectangle workingArea = InternalUtilities.getScreenWorkingArea(topWindowOrNull);
        int popupWidth = popup.getBounds().width;
        int popupHeight = popup.getBounds().height;
        // Calculate the default rectangle for the popup.
        Rectangle popupRectangle = new Rectangle(defaultX, defaultY, popupWidth, popupHeight);
        // If the popup rectangle is below the bottom of the working area, then move it upwards by 
        // the minimum amount which will ensure that it will never cover the picker component.
        if (popupRectangle.getMaxY() > (workingArea.getMaxY() + bottomOverlapAllowed)) {
            popupRectangle.y = verticalFlipReference.getLocationOnScreen().y - popupHeight
                    - verticalFlipDistance;
        }
        // Confine the popup to be within the working area.
        if (popupRectangle.getMaxX() > (workingArea.getMaxX())) {
            popupRectangle.x -= (popupRectangle.getMaxX() - workingArea.getMaxX());
        }
        if (popupRectangle.getMaxY() > (workingArea.getMaxY() + bottomOverlapAllowed)) {
            popupRectangle.y -= (popupRectangle.getMaxY() - workingArea.getMaxY());
        }
        if (popupRectangle.x < workingArea.x) {
            popupRectangle.x += (workingArea.x - popupRectangle.x);
        }
        if (popupRectangle.y < workingArea.y) {
            popupRectangle.y += (workingArea.y - popupRectangle.y);
        }
        // Set the location of the popup.
        popup.setLocation(popupRectangle.x, popupRectangle.y);
    }
}
