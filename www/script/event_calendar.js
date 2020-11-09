    var MonthName=[Strings.JANUARY,Strings.FEBRUARY,Strings.MARCH,Strings.APRIL,Strings.MAY,Strings.JUNE,Strings.JULY,Strings.AUGUST,Strings.SEPTEMBER,Strings.OCTOBER,Strings.NOVEMBER,Strings.DECEMBER];
    var WeekDayName1=[Strings.SUNDAY,Strings.MONDAY,Strings.TUESDAY,Strings.WEDNESDAY,Strings.THURSDAY,Strings.FRIDAY,Strings.SATURDAY];
    var WeekDayName2=[Strings.MONDAY,Strings.TUESDAY,Strings.WEDNESDAY,Strings.THURSDAY,Strings.FRIDAY,Strings.SATURDAY,Strings.SUNDAY];

    var LightGrayColor = "#EFEFEF";
    var DarkGrayColor = "#9D9D9D";
    var LightGreenColor = "#E2F2E0";
    var DarkGreenColor = "#C9E7C4";

    function Calendar(pDate)
    {
        this.Date=pDate.getDate();//selected date
        this.Month=pDate.getMonth();//selected month number
        this.Year=pDate.getFullYear();//selected year in 4 digits
        this.Day = pDate.getDay();
        this.today = new Date();
        this.Format="ddMMyyyy";
        this.WeekChar=1;//number of character for week day. if 2 then Mo,Tu,We. if 3 then Mon,Tue,Wed.
        this.showMonthScroller = false;
        this.showPlusIcon = true;
        this.align = "center";
        this.showWeek = false;
        this.showLongMonth=true; //Show long month name in Calendar header. example: "January".
        this.background = "white";
        this.sundayColor = LightGrayColor;
        this.saturdayColor = LightGrayColor;
        this.weekDayColor = LightGrayColor;
        this.weekHeadColor = "#B7FFAE"; //Background Color in Week header.
        this.monthColor="#008033";//color of font of Year selector.
        this.todayColor="#B7FFAE";//Background color of today.
        this.dayOfTheWeekColor = "#3F9506";
        this.weekBarHeight = 12;
        this.fontIsBold = false;
        this.mondayIsFirstDay=true;//true:Use Monday as first day; false:Sunday as first day. [true|false]
        this.textWidth = 105;
        this.textHeight = 100;
    }

    function incYear() {
        this.Year++;
    }
    Calendar.prototype.incYear=incYear;

    function decYear() {
        this.Year--;
    }
    Calendar.prototype.decYear=decYear;

    function incMonth()
    {   
        this.Month++;
        if (this.Month>=12)
        {
            this.Month=0;
            this.incYear();
        }
    }
    Calendar.prototype.incMonth=incMonth;

    function decMonth()
    {   
        this.Month--;
        if (this.Month<0)
        {
            this.Month=11;
            this.decYear();
        }
    }
    Calendar.prototype.decMonth=decMonth;

    function incWeek() {
        this.Date += 7;
        if (this.Date > this.getMonDays()) {
            this.Date -= this.getMonDays();
            this.incMonth();
        }
    }
    Calendar.prototype.incWeek = incWeek;

    function decWeek() {
        this.Date -= 7;
        if (this.Date <= 0) {
            this.decMonth();
            this.Date += this.getMonDays();
        }
    }
    Calendar.prototype.decWeek = decWeek;

    function incDate() {
        if (++this.Date > this.getMonDays()) {
            this.Date = 1;
            this.incMonth();
        }
    }
    Calendar.prototype.incDate = incDate;

    function decDate() {
        if (--this.Date == 0) {
            this.decMonth();
            this.Date = this.getMonDays();
        }
    }
    Calendar.prototype.decDate = decDate;

    function setWeekStart() {
        var day = this.Day;
        if (this.mondayIsFirstDay && this.Day == 0)
            this.Day = 6;
        this.Date = this.Date - this.Day + 1;
        if (this.Date <= 0) {
            this.decMonth();
            this.Date += this.getMonDays();
        }
    }
    Calendar.prototype.setWeekStart = setWeekStart;

    function getMonthName(month)
    {
        var Month=MonthName[month != null ? month : this.Month];
        if (this.showLongMonth)
            return Month;
        else
            return Month.substr(0,3);
    }
    Calendar.prototype.getMonthName=getMonthName;

    function getDayName() {
        return WeekDayName1[this.Day];
    }
    Calendar.prototype.getDayName=getDayName;

    function getMonDays(month) //Get number of days in a month
    {
        var DaysInMonth=[31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
        if (this.isLeapYear()) {
            DaysInMonth[1]=29;
        }
        return (month != null && month >=0 && month < 12) ? 
            DaysInMonth[month] : DaysInMonth[this.Month];
    }
    Calendar.prototype.getMonDays=getMonDays;
    
    function isLeapYear() {
        return (this.Year%4) == 0 && 
           ((this.Year%100) != 0 || (this.Year%400) == 0);
    }
    Calendar.prototype.isLeapYear=isLeapYear;
    
    function isToday(date) {
        return (date == this.today.getDate()) && 
            (this.Month == this.today.getMonth()) &&
            (this.Year==this.today.getFullYear());
    }
    Calendar.prototype.isToday=isToday;

    function formatDate() {
        var month = "" + (this.Month+1);
        var date = "" + this.Date;
        if (month.length == 1)
        	month = "0" + month;
        if (date.length == 1)
            date = "0" + date;
        return this.Year + "-" + month + "-" + date;
    }
    Calendar.prototype.formatDate=formatDate;

    function renderMonth() {
        var vCalData;
        var date;
        var weekDay=0;
        var vFirstDay;  
        var contentString = this.getTableHeader("Month") + this.getWeekHeader();

        //Calendar detail
        CalDate=new Date(this.Year,this.Month);
        CalDate.setDate(1);
        vFirstDay=CalDate.getDay();
        if (this.mondayIsFirstDay==true)
        {
            vFirstDay-=1;
            if (vFirstDay==-1)
                vFirstDay=6;
        }

        vCalData="<tr>";
        if (this.showWeek) {
            // if week starts in previous month
            var date = this.Date;
            var month = this.Month;
            var year = this.Year;
            if (vFirstDay > 0) {
                if (--month < 0) {
                    month = 11;
                    year--;
                }
                date = this.getMonDays(month) - vFirstDay + 1;
            }
            vCalData += "<td class=\"weekTitle\" align=\"center\">" +
                "<a style=\"color: white\" " +
                "href=\"JavaScript:selectWeek(" +
                year + "," + (month+1) + "," + date + ")\">" +
                this.formatWeekString() + "<br>1</a></td>";
        }
        for (weekDay = 0; weekDay < vFirstDay; weekDay++) {
            vCalData += this.genCell();
        }
        rows = 0;
        d = new Date(this.Year, this.Month, 1);
        for (date = 1; date <= this.getMonDays(); date++) {
            d.setDate(date);
            vCalData += this.dayCell(d);
            if( (++weekDay%7 == 0) && (date < this.getMonDays())) {
                vCalData += "</tr>\n<tr>";
                if (this.showWeek) {
                    vCalData += "<td align=\"center\" class=\"weekTitle\">" +
                        "<a style=\"color: white\" href=\"" +
                        "JavaScript:selectWeek(" + this.Year + "," + 
                        (this.Month+1) + "," + (date+1) + ")\">" +
                        this.formatWeekString() + "<br/>" + (rows+2) + "</td>";
                }
                rows++;
            }
        }
        while (weekDay % 7 != 0) {
            vCalData += this.genCell();
            weekDay++;
        }
        vCalData=vCalData+"</tr>";
        while (rows < 5 && !this.showMonthScroller) {
            vCalData += "</tr>\n<tr><td><big>&#160</big></td></tr>";
            rows++;
        }
        contentString += vCalData;
        contentString += "</table>";
        return contentString;
    }
    Calendar.prototype.renderMonth=renderMonth;

    function renderWeek() {
        var vCalData;
        var date;
        var weekDay;
        var contentString = this.getTableHeader("Week") + this.getWeekHeader();
        vCalData="<tr>";
        if (this.showWeek)
            vCalData += "<td class=\"weekTitle\" align=\"center\">" +
                "&#160;&#160;&#160;</td>";
        year = this.Year;
        month = this.Month;
        date = this.Date;
        d = new Date();
        for (weekDay = 1; weekDay <= 7; weekDay++) {
            d.setYear(year);
            d.setMonth(month);
            d.setDate(date++);
            vCalData += this.dayCell(d);
            if (date > this.getMonDays()) {
               date = 1;
               if (++month > 11) {
                   month = 0;
                   year++;
               }
            }
        }
        contentString += vCalData + "</tr></table>";
        return contentString;
    }
    Calendar.prototype.renderWeek=renderWeek;

    function getTableHeader(name) {
        var vCalHeader = "<table height=\"100%\" border=0 cellpadding=0 " +
			"cellspacing=3 width=\"100%\" valign=\"top\" class=\"admGrnBorder" +
			"\">";
        vCalHeader += "<tr height=\"16\"><td colspan=\"8\">" +
        	"<table border=0 width=\"100%\" cellpadding=0 cellspacing=0><tr>";
        if (this.showMonthScroller) {
            vCalHeader += "<td class=\"admNavbarImg\"><img src=\"/images/" +
                "titleLeft.gif\" alt=\"\" border=0/></td>";
            vCalHeader += "<td class=\"admTitle admLeft\" align=\"center\"" +
                " style=\"font-size: 12px;\"><a id=\"scr\" class=\"admNone\"" +
                " href=\"JavaScript:prev" + name + "();\"><small>&lt;&lt;&lt;" +
                "</small> " + Strings.PREV + "</a></td>";
            var title = name == "Week" ? this.getWeekTitle() :
                this.getMonthName() + " " + this.Year;
            vCalHeader += "<td class=\"admTitle\"><b>"+ title + 
                "</b></font></td>";
            vCalHeader += "<td class=\"admTitle admRight\" align=\"center\"" +
                " style=\"font-size: 12px;\"><a class=\"admNone\"" +
                " href=\"JavaScript:next" + name + "();\">" + Strings.NEXT +
                " <small> &gt;&gt;&gt;</small></a></td>";
            vCalHeader += "<td class=\"admNavbarImg\"><img src=\"/images/" +
                "titleRight.gif\" alt=\"\" border=0/></td>";
        }
        else {
            vCalHeader += "<td align=\"center\" width=\"90%\">" +
                "<font face=\"Verdana\" size=2><b>";
            vCalHeader += "<a style=\"color : " + this.monthColor + 
                "\" href=\"JavaScript:selectMonth(" + this.Year + "," + 
                (this.Month+1) + ");\">";
            vCalHeader += this.getMonthName()+" " + this.Year + "</a></b>" +
                "</font></td>";
        }
        vCalHeader += "</tr></table></td></tr>"
        return vCalHeader;
    }
    Calendar.prototype.getTableHeader = getTableHeader;

    /* Builds the header of the calendar with the days names */
    function getWeekHeader() {
        var header = "<tr height=\"" + this.weekBarHeight + 
            "\" bgcolor=" + this.weekHeadColor+">";
        if (this.showWeek)
            header += "<td class=\"weekTitle\" width=20></td>";
        var WeekDayName = this.mondayIsFirstDay ? WeekDayName2 : WeekDayName1;
        for (i = 0; i < 7; i++) {
            header += "<td align=\"center\" style=\"padding: 2px;\">" +
               "<font face=\"Verdana\" size=\"2\" color=\"" + 
               this.dayOfTheWeekColor + "\"><b>" + 
               WeekDayName[i].substr(0,this.WeekChar)+"</b></font></td>";
        }
        header += "</tr>";
        return header;
    }
    Calendar.prototype.getWeekHeader = getWeekHeader;

    function getWeekTitle() {
        var month = this.Month;
        var year = this.Year;
        var title = "";
        if (this.getMonDays() - this.Date < 6) {
            if (++month > 11) {
                month = 0;
                year++;
            }
        }
        if (year != this.Year) {
            title = this.getMonthName() + " " + this.Year + " / " +
                 this.getMonthName(month) + " " + year;
        }
        else if (month != this.Month) {
            title = this.getMonthName() + " / " +
                 this.getMonthName(month) + " " + this.Year;
        }
        else
            title = this.getMonthName() + " " + this.Year;
        return title;
    }
    Calendar.prototype.getWeekTitle = getWeekTitle;

    /* Builds the html-code for one day of the period */
    function dayCell(d) {
        var strCell;
        var date = d.getDate();
        var weekDay = d.getDay();
        if (this.isToday(date)) //Highlight today's date
            strCell=this.genCell(d, true, this.todayColor);
        else {
            if (this.mondayIsFirstDay) {
                if (weekDay%7 == 0)
                    strCell=this.genCell(d, false, this.sundayColor);
                else if ((weekDay+1)%7 == 0)
                    strCell=this.genCell(d, false, this.saturdayColor);
                else
                    strCell=this.genCell(d, null, this.weekDayColor);
            }
            else {
                if (weekDay%7 == 0)
                    strCell=this.genCell(d, false, this.saturdayColor);
                else if ((weekDay+6)%7 == 0)
                    strCell=this.genCell(d, false, this.sundayColor);
                else
                    strCell=this.genCell(d, null, this.weekDayColor);
            }
        }
        return strCell;
    }
    Calendar.prototype.dayCell = dayCell;

    //Generate table cell with value
    function genCell(d, pHighLight, pColor) {
        var date;
        var PCellStr;
        var vColor;
        date = d != null ? d.getDate() : "";
        vColor = pColor != null ? "bgcolor=\""+pColor+"\"" : 
            "bgcolor=\""+this.background+"\"";
        var idStr = "";
        var aStr = "";
        if (this.showWeek && d != null) {
        	if (this.showPlusIcon) {
	            aStr = "<a title=\"" + Strings.ADD_EVENT +
    	            "\" href=\"/admin/add_event?startDate=" + d.getYear() + "-" +
        	        (d.getMonth()+1) + "-" + date + "\">"
	            aStr += "<img src=\"/images/plus.gif\" hspace=10 border=0></a>";
			}
            aStr += "<div style=\"overflow : hidden; height:" + 
                this.textHeight + "px; width:" + this.textWidth + 
                "px\" id=\"day_" + date + "\"></div>";
            idStr = "id=\"cell_" + date + "\"";
        }
        PCellStr = "<td " + idStr + " valign=\"top\" "+vColor+" align= \"" + 
            this.align + "\"><font face=\"verdana\" size=2>";
        if (d != null) {
           var title = date + " " + this.getMonthName(d.getMonth()) + 
              " " + d.getYear();
           if (this.fontIsBold)
              PCellStr += "<b>";
           PCellStr += "<a title=\"" + title + "\"" +
              "href=\"JavaScript:selectDate(" + d.getYear() + "," + 
              (d.getMonth()+1) + "," + date + ")\">"+date+"</a>";
            if (this.fontIsBold)
              PCellStr += "</b>";
        }
        PCellStr += "</font>" + aStr + "</td>";
        return PCellStr;
    }
    Calendar.prototype.genCell=genCell;

    function formatWeekString() {
        var s = "";
        for (i = 0; i < Strings.WEEK.length; i++)
            s += Strings.WEEK.charAt(i) + "<br>";
        return s;
    }
    Calendar.prototype.formatWeekString=formatWeekString;

    function getDate(dateStr) {
        if(/^(\d{1,4})\-(\d{1,2})\-(\d{1,2})$/.test(dateStr)){
            year=parseInt(RegExp.$1,10);
            if(year<100) year=(year<70)?2000+year:1900+year;
            month=RegExp.$2-1;
            date=parseInt(RegExp.$3,10);
            d1=new Date(year, month, date);
            return d1;
        }
    }
