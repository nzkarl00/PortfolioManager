function getSprintTimeBound(timeBoundList, start, end) {
    let startDate = Date.parse(start)
    let endDate = Date.parse(end)+86399999
    let returning = [];
    for (const timeBoundItem of timeBoundList) {
        let date = Date.parse(timeBoundItem.startDate)
        if (date >= startDate && date <= endDate) {
            returning.push(timeBoundItem)
        }
    }
    return returning
}

function addItem(list, item){
    if (!list.includes(item)) {
        list.push(item);
    }
}

function getSprintEvent(timeBoundList, start, end) {
    let startDate = Date.parse(start)
    let endDate = Date.parse(end)+86399999
    let returning = [];
    for (const timeBoundItem of timeBoundList) {
        let date = Date.parse(timeBoundItem.startDate)
        let endEvent = Date.parse(timeBoundItem.endDate)
        if (date >= startDate && date <= endDate) {
            addItem(returning, timeBoundItem)
        }
        if (endEvent >= startDate && endEvent <= endDate) {

            addItem(returning, timeBoundItem)
        }
    }
    return returning
}

function getBetweenTimeBound(timeBoundList, start, end, position) {
    let startDate = Date.parse(start)+86399999
    let endDate = Date.parse(end)
    let returning = [];
    for (const timeBoundItem of timeBoundList) {
        let date = Date.parse(timeBoundItem.startDate)
        if (position == "start") {
            returning.push(timeBoundItem)
        } else if (position == "middle") {
            if (date > startDate && date < endDate) {
                returning.push(timeBoundItem)
            }
        } else {
            if (date > startDate && date <= endDate) {
                returning.push(timeBoundItem)
            }
        }
    }
    return returning
}

function getBetweenEvent(timeBoundList, start, end, position) {
    let startDate = Date.parse(start)+86399999
    let endDate = Date.parse(end)
    let returning = [];
    for (const timeBoundItem of timeBoundList) {
        let date = Date.parse(timeBoundItem.startDate)
        let endEvent = Date.parse(timeBoundItem.endDate)
        if (position == "start") {
            if (date >= startDate - 86399999 && date < endDate) {
                addItem(returning, timeBoundItem)
            }
            if (endEvent >= startDate - 86399999 && endEvent < endDate) {
                addItem(returning, timeBoundItem)
            }
        } else if (position == "middle") {
            if (date > startDate && date < endDate) {
                addItem(returning, timeBoundItem)
            }
            if (endEvent > startDate && endEvent < endDate) {
                addItem(returning, timeBoundItem)
            }
        } else {
            if (date > startDate && date <= endDate) {
                addItem(returning, timeBoundItem)
            }
            if (endEvent > startDate && endEvent <= endDate) {
                addItem(returning, timeBoundItem)
            }
        }
    }
    return returning
}

function getCalendarTimeBound(timeBoundList, start) {
    let startDate = Date.parse(start)
    let endDate = Date.parse(start) + 86399999
    let returning = [];
    for (const timeBoundItem of timeBoundList) {
        let date = Date.parse(timeBoundItem.startDate)
        if (date >= startDate && date <= endDate) {
            returning.push(timeBoundItem)
        }
    }
    return returning
}