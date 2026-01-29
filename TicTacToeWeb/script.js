const board = document.getElementById('board');
const cells = document.querySelectorAll('.cell');
const statusText = document.getElementById('status');
const restartBtn = document.getElementById('restartBtn');

let currentPlayer = 'X';
let gameBoard = ["", "", "", "", "", "", "", "", ""];
let gameActive = true;

const winningConditions = [
    [0, 1, 2],
    [3, 4, 5],
    [6, 7, 8],
    [0, 3, 6],
    [1, 4, 7],
    [2, 5, 8],
    [0, 4, 8],
    [2, 4, 6]
];

function handleCellClick(e) {
    const clickedCell = e.target;
    const clickedCellIndex = parseInt(clickedCell.getAttribute('data-index'));

    if (gameBoard[clickedCellIndex] !== "" || !gameActive) {
        return;
    }

    updateCell(clickedCell, clickedCellIndex);
    checkResult();
}

function updateCell(cell, index) {
    gameBoard[index] = currentPlayer;
    cell.textContent = currentPlayer;
    cell.classList.add(currentPlayer.toLowerCase()); // Adds 'x' or 'o' class for styling
    cell.style.opacity = '0';
    requestAnimationFrame(() => {
        cell.style.transition = 'opacity 0.2sease-in';
        cell.style.opacity = '1';
    });
}

function changePlayer() {
    currentPlayer = currentPlayer === 'X' ? 'O' : 'X';
    const playerClass = currentPlayer === 'X' ? 'player-x' : 'player-o';
    statusText.innerHTML = `Player <span class="${playerClass}">${currentPlayer}</span>'s Turn`;
}

function checkResult() {
    let roundWon = false;
    let winningCells = [];

    for (let i = 0; i < winningConditions.length; i++) {
        const winCondition = winningConditions[i];
        let a = gameBoard[winCondition[0]];
        let b = gameBoard[winCondition[1]];
        let c = gameBoard[winCondition[2]];

        if (a === '' || b === '' || c === '') {
            continue;
        }

        if (a === b && b === c) {
            roundWon = true;
            winningCells = winCondition;
            break;
        }
    }

    if (roundWon) {
        highlightWinningCells(winningCells);
        const playerClass = currentPlayer === 'X' ? 'player-x' : 'player-o';
        statusText.innerHTML = `Player <span class="${playerClass}">${currentPlayer}</span> Wins!`;
        gameActive = false;
        return;
    }

    if (!gameBoard.includes("")) {
        statusText.innerText = "Game ended in a Draw!";
        gameActive = false;
        return;
    }

    changePlayer();
}

function highlightWinningCells(indices) {
    indices.forEach(index => {
        cells[index].classList.add('winner');
    });
}

function restartGame() {
    currentPlayer = 'X';
    gameBoard = ["", "", "", "", "", "", "", "", ""];
    gameActive = true;
    const playerClass = 'player-x';
    statusText.innerHTML = `Player <span class="${playerClass}">${currentPlayer}</span>'s Turn`;

    cells.forEach(cell => {
        cell.textContent = "";
        cell.classList.remove('x', 'o', 'winner');
        cell.style.opacity = '1';
    });
}

cells.forEach(cell => cell.addEventListener('click', handleCellClick));
restartBtn.addEventListener('click', restartGame);
