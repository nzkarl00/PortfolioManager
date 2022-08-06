// build a sprint in html from js
function buildSprint(sprint) {
    const mainDiv = document.getElementById("sprints")
    // make the main container for all the sprints
    const sprintDiv = document.createElement("div")
    sprintDiv.id = "sprint" + sprint.id
    sprintDiv.className = "row row_sprints";
    mainDiv.appendChild(sprintDiv)

    const col2 = document.createElement("div")
    col2.className = "col-2"
    sprintDiv.appendChild(col2)

    const col1 = document.createElement("div")
    col1.className = "col-lg-1 col-md-2 col-sm-12 col-12 align-items-end "
    sprintDiv.appendChild(col1)

    const label = document.createElement("div")
    label.className = "portion_sprintbody project_sprinthead "
    label.innerText = sprint.label
    col1.appendChild(label)

    const editButton = document.createElement("button")
    editButton.className = "edit"
    col1.appendChild(editButton)

    const innerEditButton = document.createElement("a")
    innerEditButton.className = "button_a"
    innerEditButton.innerText = "EDIT"
    innerEditButton.href = "edit-sprint?id=" + sprint.parentProjectId + "&ids=" + sprint.id
    editButton.appendChild(innerEditButton)

    const deleteForm = document.createElement("form")
    deleteForm.method = "post"
    deleteForm.action = "delete-sprint"
    col1.appendChild(deleteForm)

    const deleteProjectId = document.createElement("input")
    deleteProjectId.style = "display:none;"
    deleteProjectId.name = "deleteprojectId"
    deleteProjectId.id = "deleteprojectId"
    deleteProjectId.value = sprint.parentProjectId
    deleteProjectId.type = "number"
    deleteForm.appendChild(deleteProjectId)

    const deleteSprintId = document.createElement("input")
    deleteSprintId.style = "display:none;"
    deleteSprintId.name = "sprintId"
    deleteSprintId.id = "sprintId"
    deleteSprintId.value = sprint.id
    deleteSprintId.type = "number"
    deleteForm.appendChild(deleteSprintId)

    const deleteButton = document.createElement("button")
    deleteButton.className = "delete delete-button "
    deleteButton.innerText = "DELETE"
    deleteForm.appendChild(deleteButton)

    const br1 = document.createElement("br")
    const br2 = document.createElement("br")
    col1.appendChild(br1)
    col1.appendChild(br2)

    const col3 = document.createElement("div")
    col3.className = "col-lg-7 col-md-8 col-sm-12 portion_body"
    col3.style.border = "5px solid " + colors[sprint.id % colors.length]
    sprintDiv.appendChild(col3)

    const name = document.createElement("div")
    name.id = "sprintName"
    name.className = "sprint-title project_subhead "
    name.innerText = sprint.name
    col3.appendChild(name)

    const date = document.createElement("div")
    date.id = "sprintDate"
    date.className = "sprint-date display_data"
    date.innerText = sprint.startDateString + "-" + sprint.endDateString
    col3.appendChild(date)

    const description = document.createElement("div")
    description.id = "sprintDesc"
    description.className = "sprint-description display_data"
    description.innerText = "Description: " + sprint.description
    col3.appendChild(description)

    const events = document.createElement("div")
    events.className = "project_subhead sprint-description display_data"
    events.id = "events" + sprint.id
    events.innerHTML = '<i class="fa fa-star"></i>' + " "  + "Events:"
    col3.appendChild(events)

    const eventsList = document.createElement("div")
    eventsList.id = "eventsList" + sprint.id
    col3.appendChild(eventsList)

    const deadlines = document.createElement("div")
    deadlines.className = "project_subhead sprint-description display_data"
    deadlines.id = "deadlines" + sprint.id
    deadlines.innerHTML = '<i class="fa fa-calendar-o"></i>' + " " + "Deadlines:"
    col3.appendChild(deadlines)

    const deadlinesList = document.createElement("div")
    deadlinesList.id = "deadlinesList" + sprint.id
    col3.appendChild(deadlinesList)

    const milestones = document.createElement("div")
    milestones.className = "project_subhead sprint-description display_data"
    milestones.id = "milestones" + sprint.id
    milestones.innerHTML = '<i class="fa fa-flag"></i>' + " "  + "Milestones:"
    col3.appendChild(milestones)

    const milestonesList = document.createElement("div")
    milestonesList.id = "milestonesList" + sprint.id
    col3.appendChild(milestonesList)


    const col4 = document.createElement("div")
    col4.className = "col-2"
    sprintDiv.appendChild(col4)

    if(!canEdit) {
        editButton.style.display = "none"
        deleteButton.style.display = "none"
    }
}

function buildDeadline(deadline, sprintId, sprintProjectId) {
    const mainDiv = document.getElementById("deadlinesList" + sprintId)
    const eachDeadline = document.createElement("form")
    const deleteDeadline = document.createElement("button")
    const editDeadline = document.createElement("button")
    const deadlineType = document.createElement("input")
    const projectId = document.createElement("input")
    const deadlineId = document.createElement("input")
    const dateId = document.createElement("input")


    dateId.style = "display:none;"
    dateId.name = "dateId"
    dateId.id = "dateId"
    dateId.value = deadline.id
    dateId.type = "number"

    projectId.style = "display:none;"
    projectId.name = "projectId"
    projectId.value = sprintProjectId
    projectId.id = "projectId"
    projectId.type = "number"

    deadlineId.style = "display:none;"
    deadlineId.name = "deadlineId"
    deadlineId.id = "deadlineId"
    deadlineId.value = deadline.id
    deadlineId.type = "number"

    deadlineType.style = "display:none;"
    deadlineType.name = "itemType"
    deadlineType.id = "itemType"
    deadlineType.value = "Deadline"
    deadlineType.type = "text"

    editDeadline.type = "submit"
    editDeadline.className = "fa fa-edit each-deadline-edit"
    editDeadline.formMethod = "get"
    editDeadline.formAction = "edit-date"

    deleteDeadline.type = "submit"
    deleteDeadline.id = "deleteButton"
    deleteDeadline.className = "fa fa-trash each-deadline"

    eachDeadline.method = "post"
    eachDeadline.action = "delete-deadline"
    eachDeadline.innerHTML = '<i class="fa fa-calendar-o"></i>' + " " + deadline.name
    eachDeadline.className = "posAbsolute increase_size"

    const deadlineDate = String(deadline.startDate).substring(0,10);
    const deadlineTime = String(deadline.startDate).substring(11);

    var deadlineTooltip;
    if (deadline.description !== 'undefined') {
        deadlineTooltip = '<p class="tooltip_title">' + deadline.name.substring(0,25) + "</p>" + '<p class="tooltip_body colour'+sprintId % colors.length+'" >Due: ' + deadlineDate + '</p> <p class="tooltip_body">' + "At: " + deadlineTime + '</p> <p class="tooltip_body">' + deadline.description + "</p>"
    } else {
        deadlineTooltip = '<p class="tooltip_title">' + deadline.name.substring(0,25) + "</p>" + '<p class="tooltip_body colour'+sprintId % colors.length+'" >Due:' + deadlineDate + '</p> <p class="tooltip_body">' + "At: " + deadlineTime + "</p>"
    }

    $(eachDeadline).tooltip({
        title: deadlineTooltip,
        placement: "right",
        trigger: "hover",
        container: "body",
        html: true
    });

    if(!canEdit) {
        editDeadline.style.display = "none"
        deleteDeadline.style.display = "none"
    }

    mainDiv.appendChild(eachDeadline)
    eachDeadline.appendChild(projectId)
    eachDeadline.appendChild(deadlineId)
    eachDeadline.appendChild(dateId)
    eachDeadline.appendChild(editDeadline)
    eachDeadline.appendChild(deadlineType)
    eachDeadline.appendChild(deleteDeadline)
}

function getSprintStartIds(event) {
    let eventStart = Date.parse(event.startDate)
    let startId = -1;
    for (const sprint of sprintList) {
        let sprintStart = Date.parse(sprint.startDate)
        let sprintEnd = Date.parse(sprint.endDate)
        if (eventStart >= sprintStart && eventStart < sprintEnd+86400000){
            startId = parseInt(sprint.id) % colors.length
        }
    }
    return startId
}
function getSprintEndIds(event) {
    let eventEnd = Date.parse(event.endDate)
    let endId = -1;
    for (const sprint of sprintList) {
        let sprintStart = Date.parse(sprint.startDate)
        let sprintEnd = Date.parse(sprint.endDate)
        if (eventEnd >= sprintStart && eventEnd < sprintEnd+86400000){
            endId = parseInt(sprint.id) % colors.length
        }
    }
    return endId
}

function buildEvent(event, sprintId, sprintProjectId) {
    const mainDiv = document.getElementById("eventsList" + sprintId)
    const eachEvent = document.createElement("form")
    const deleteEvent = document.createElement("button")
    const editEvent = document.createElement("button")
    const projectId = document.createElement("input")
    const eventId = document.createElement("input")
    const eventType = document.createElement("input")


    projectId.style = "display:none;"
    projectId.name = "projectId"
    projectId.value = sprintProjectId
    projectId.id = "projectId"
    projectId.type = "number"

    eventId.style = "display:none;"
    eventId.name = "dateId"
    eventId.id = "eventId"
    eventId.value = event.id
    eventId.type = "number"

    eventType.style = "display:none;"
    eventType.name = "itemType"
    eventType.id = "itemType"
    eventType.value = "Event"
    eventType.type = "text"

    editEvent.type = "submit"
    editEvent.className = "fa fa-edit each-deadline-edit"
    editEvent.formMethod = "get"
    editEvent.formAction = "edit-date"

    deleteEvent.type = "submit"
    deleteEvent.id = "deleteButton"
    deleteEvent.className = "fa fa-trash each-deadline"

    eachEvent.method = "post"
    eachEvent.action = "delete-event"
    eachEvent.innerHTML = '<i class="fa fa-star"></i>' + " "  + event.name
    eachEvent.className = "posAbsolute increase_size"

    const eventStartDate = String(event.startDate).substring(0,10);
    const eventEndDate = String(event.endDate).substring(0,10);
    const eventStartTime = "at " + String(event.startDate).substring(11,16);
    const eventEndTime = "at " + String(event.endDate).substring(11,16);
    var eventTooltip;

    const sprintId1 = getSprintStartIds(event);
    const sprintId2 = getSprintEndIds(event);
    if (event.description !== undefined) {
        eventTooltip = '<p class="tooltip_title">' + event.name.substring(0,25) + "</p>" + '<p class="tooltip_body colour'+(sprintId1)+'" >Start Date: ' + eventStartDate + '</p> <p class="tooltip_body">' + 'Starts at: ' + eventStartTime + '</p> <p class="tooltip_body colour'+(sprintId2) +'" >' + "End Date: " + eventEndDate + '</p> <p class="tooltip_body">' + 'Starts at: ' + eventEndTime + '</p> <p class="tooltip_body">' + event.description + "</p>"
    } else {
        eventTooltip = '<p class="tooltip_title">' + event.name.substring(0,25) + "</p>" + '<p class="tooltip_body colour'+(sprintId1)+'" >Start Date: ' + eventStartDate + '</p> <p class="tooltip_body">' + 'Starts at: ' + eventStartTime + '</p> <p class="tooltip_body colour'+(sprintId2) +'" >' + "End Date: " + eventEndDate + '</p> <p class="tooltip_body">' + 'Ends at: ' + eventEndTime + '</p>'
    }

    $(eachEvent).tooltip({
        title: eventTooltip,
        placement: "right",
        trigger: "hover",
        container: "body",
        html: true,
    });

    if(!canEdit) {
        editEvent.style.display = "none"
        deleteEvent.style.display = "none"
    }

    mainDiv.appendChild(eachEvent)
    eachEvent.appendChild(projectId)
    eachEvent.appendChild(eventId)
    eachEvent.appendChild(editEvent)
    eachEvent.appendChild(deleteEvent)
    eachEvent.appendChild(eventType)
}

function buildMilestone(milestone, sprintId, sprintProjectId) {
    const mainDiv = document.getElementById("milestonesList" + sprintId)
    const eachMilestone = document.createElement("form")
    const deleteMilestone = document.createElement("button")
    const editMilestone = document.createElement("button")
    const milestoneType = document.createElement("input")
    const projectId = document.createElement("input")
    const milestoneId = document.createElement("input")
    const dateId = document.createElement("input")


    dateId.style = "display:none;"
    dateId.name = "dateId"
    dateId.id = "dateId"
    dateId.value = milestone.id
    dateId.type = "number"

    projectId.style = "display:none;"
    projectId.name = "projectId"
    projectId.value = sprintProjectId
    projectId.id = "projectId"
    projectId.type = "number"

    milestoneId.style = "display:none;"
    milestoneId.name = "milestoneId"
    milestoneId.id = "milestoneId"
    milestoneId.value = milestone.id
    milestoneId.type = "number"

    milestoneType.style = "display:none;"
    milestoneType.name = "itemType"
    milestoneType.id = "itemType"
    milestoneType.value = "Milestone"
    milestoneType.type = "text"

    editMilestone.type = "submit"
    editMilestone.className = "fa fa-edit each-deadline-edit"
    editMilestone.formMethod = "get"
    editMilestone.formAction = "edit-date"

    deleteMilestone.type = "submit"
    deleteMilestone.id = "deleteButton"
    deleteMilestone.className = "fa fa-trash each-milestone"

    eachMilestone.method = "post"
    eachMilestone.action = "delete-milestone"
    eachMilestone.innerHTML = '<i class="fa fa-flag"></i>' + " " + milestone.name
    eachMilestone.className = "posAbsolute increase_size"

    const milestoneDate = String(milestone.startDate).substring(0,10);
    var milestoneToolTip;
    if (milestone.description !== undefined) {
        milestoneToolTip = '<p class="tooltip_title">' + milestone.name.substring(0,25) + "</p>" + '<p class="tooltip_body colour'+sprintId % colors.length+'" >By: ' + milestoneDate + '</p> <p class="tooltip_body">' + milestone.description + "</p>"
    } else {
        milestoneToolTip = '<p class="tooltip_title">' + milestone.name.substring(0,25) + "</p>" + '<p class="tooltip_body colour'+sprintId % colors.length+'" >By: ' + milestoneDate + '</p>'
    }

    $(eachMilestone).tooltip({
        title: milestoneToolTip,
        placement: "right",
        trigger: "hover",
        container: "body",
        html: true
    });

    if(!canEdit) {
        editMilestone.style.display = "none"
        deleteMilestone.style.display = "none"
    }

    mainDiv.appendChild(eachMilestone)
    eachMilestone.appendChild(projectId)
    eachMilestone.appendChild(milestoneId)
    eachMilestone.appendChild(milestoneType)
    eachMilestone.appendChild(editMilestone)
    eachMilestone.appendChild(dateId)
    eachMilestone.appendChild(deleteMilestone)
}

// build an in-between sprint in html from js
function buildInBetweenSprint(startDateDate, endDateDate, id, deadlineList, eventList, milestoneList, projectId) {

    const startDate = startDateDate.toString();
    const endDate = endDateDate.toString();

    const mainDiv = document.getElementById("sprints")
    // make the main container for all the sprints
    const sprintDiv = document.createElement("div")
    sprintDiv.id = "sprint" + id
    sprintDiv.className = "row row_sprints";
    mainDiv.appendChild(sprintDiv)

    const col2 = document.createElement("div")
    col2.className = "col-2"
    sprintDiv.appendChild(col2)

    const col1 = document.createElement("div")
    col1.className = "col-lg-1 col-md-2 col-sm-12 col-12 align-items-end "
    sprintDiv.appendChild(col1)

    const br1 = document.createElement("br")
    const br2 = document.createElement("br")
    col1.appendChild(br1)
    col1.appendChild(br2)

    const col3 = document.createElement("div")
    col3.className = "col-lg-7 col-md-8 col-sm-12 portion_body"
    col3.style.border = "5px solid #19DAFF"
    sprintDiv.appendChild(col3)

    const events = document.createElement("div")
    events.className = "project_subhead sprint-description display_data"
    events.id = "events" + id
    events.innerHTML = '<i class="fa fa-star"></i>' + " "  + "Events:"
    col3.appendChild(events)

    const eventsList = document.createElement("div")
    eventsList.id = "eventsList" + id
    col3.appendChild(eventsList)

    const deadlines = document.createElement("div")
    deadlines.className = "project_subhead sprint-description display_data"
    deadlines.id = "deadlines" + id
    deadlines.innerHTML = '<i class="fa fa-calendar-o"></i>' + " " + "Deadlines:"
    col3.appendChild(deadlines)

    const deadlinesList = document.createElement("div")
    deadlinesList.id = "deadlinesList" +id
    col3.appendChild(deadlinesList)

    const milestones = document.createElement("div")
    milestones.className = "project_subhead sprint-description display_data"
    milestones.id = "milestones" + id
    milestones.innerHTML = '<i class="fa fa-flag"></i>' + " "  + "Milestones:"
    col3.appendChild(milestones)

    const milestonesList = document.createElement("div")
    milestonesList.id = "milestonesList" + id
    col3.appendChild(milestonesList)


    const col4 = document.createElement("div")
    col4.className = "col-2"
    sprintDiv.appendChild(col4)

    var emptyLists = 0;

    let tempDeadlines = getSprintTimeBound(deadlineList, startDateDate, endDateDate)
    if (tempDeadlines.length == 0) {
        emptyLists += 1;
        deadlines.style.display = "none"
        deadlinesList.style.display = "none"
    } else {
        for (const deadline of tempDeadlines) {
            buildDeadline(deadline, id, projectId)
        }
    }

    let tempMilestones = getSprintTimeBound(milestoneList, startDateDate, endDateDate)
    if (tempMilestones.length == 0) {
        emptyLists += 1;
        milestones.style.display = "none"
        milestonesList.style.display = "none"
    } else {
        for (const milestone of tempMilestones) {
            buildMilestone(milestone, id, projectId)
        }
    }

    let tempEvents = getSprintTimeBound(eventList, startDateDate, endDateDate)
    if (tempEvents.length == 0) {
        emptyLists += 1;
        events.style.display = "none"
        eventsList.style.display = "none"
        if (emptyLists == 3) {
            sprintDiv.style.display = "none"
        }
    } else {
        for (const event of tempEvents) {
            buildEvent(event, id, projectId)
        }
    }
}