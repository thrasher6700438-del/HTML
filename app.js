function clearScreen() {
    document.getElementById("result").value = "";
}

// appends the click button value to the display
function setScreenValue() {
    const r = document.getElementById("result");
    if (r.value === "Enter an expression" || r.value === "Invalid Expression") r.value = "";
}
// Calculates and displays the result
function calculateResult() {
    const resultElement = document.getElementById("result");
    const expression = resultElement.value.trim();

    // Check for empty input
    if (expression === '') {
        resultElement.value = 'Enter an expression';
        return;
    }
}
// Evaluate expression and handle errors
try {
    resultElement.value = eval (expression);
} catch (e) {
    resultElement.value = 'Invalid Expression';
}
