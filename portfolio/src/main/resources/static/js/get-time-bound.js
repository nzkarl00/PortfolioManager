function getSprintTimeBound(timeBoundList, start, end) {
    let startDate = Date.parse(start)
    let endDate = Date.parse(end)
    let returning = [];
    for (const timeBoundItem of timeBoundList) {
        let date = Date.parse(timeBoundItem.startDate)
        if (date >= startDate && date <= endDate) {
            returning.push(timeBoundItem)
        }
    }
    return returning
}

function getBetweenTimeBound(timeBoundList, start, end, position) {
    let startDate = Date.parse(start)
    let endDate = Date.parse(end)
    let returning = [];
    for (const timeBoundItem of timeBoundList) {
        let date = Date.parse(timeBoundItem.startDate)
        if (position == "start") {
            if (date >= startDate && date < endDate) {
                returning.push(timeBoundItem)
            }
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

function getCalendarTimeBound(timeBoundList, start) {
    let startDate = Date.parse(start)
    let endDate = Date.parse(start)+86399999
    let returning = [];
    for (const timeBoundItem of timeBoundList) {
        let date = Date.parse(timeBoundItem.startDate)
        if (date >= startDate && date <= endDate) {
            returning.push(timeBoundItem)
        }
    }
    return returning
}