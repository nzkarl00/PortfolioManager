function updateTitle(element_id, maxlen_num, text_id) {
    let maxLen = maxlen_num;
    let titleVal = (document.getElementById(element_id));
    let titleLen = titleVal.value.length;

    if ((maxLen - titleLen) < 0) {
        document.getElementById(text_id).innerHTML = "Your title is " + (Math.abs(maxLen - titleLen)).toString() + " characters too long";
        document.getElementById(text_id).classList.add('project_inputAlertRed');
        document.getElementById(text_id).classList.remove('project_inputAlertBlue');
    } else {
        document.getElementById(text_id).innerHTML = "Characters Remaining: " + (maxLen - titleLen).toString();
        document.getElementById(text_id).classList.add('project_inputAlertBlue');
        document.getElementById(text_id).classList.remove('project_inputAlertRed');
    }

}