import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { RefreshCcw, X, Circle, Trophy, User } from 'lucide-react';
import './App.css';

type Player = 'X' | 'O' | null;

const App: React.FC = () => {
  const [board, setBoard] = useState<Player[]>(Array(9).fill(null));
  const [isXNext, setIsXNext] = useState<boolean>(true);
  const [winner, setWinner] = useState<Player | 'Draw'>(null);
  const [winningLine, setWinningLine] = useState<number[] | null>(null);

  const calculateWinner = (squares: Player[]) => {
    const lines = [
      [0, 1, 2], [3, 4, 5], [6, 7, 8], // Rows
      [0, 3, 6], [1, 4, 7], [2, 5, 8], // Cols
      [0, 4, 8], [2, 4, 6]             // Diagonals
    ];

    for (let i = 0; i < lines.length; i++) {
      const [a, b, c] = lines[i];
      if (squares[a] && squares[a] === squares[b] && squares[a] === squares[c]) {
        return { winner: squares[a], line: lines[i] };
      }
    }

    if (!squares.includes(null)) {
      return { winner: 'Draw' as const, line: null };
    }

    return null;
  };

  const handleClick = (index: number) => {
    if (board[index] || winner) return;

    const newBoard = [...board];
    newBoard[index] = isXNext ? 'X' : 'O';
    setBoard(newBoard);
    setIsXNext(!isXNext);

    const result = calculateWinner(newBoard);
    if (result) {
      setWinner(result.winner);
      setWinningLine(result.line);
    }
  };

  const resetGame = () => {
    setBoard(Array(9).fill(null));
    setIsXNext(true);
    setWinner(null);
    setWinningLine(null);
  };

  return (
    <div className="app-wrapper">
      <div className="background-globes">
        <div className="globe globe-1"></div>
        <div className="globe globe-2"></div>
        <div className="globe globe-3"></div>
      </div>

      <div className="game-container">
        <motion.header
          initial={{ y: -50, opacity: 0 }}
          animate={{ y: 0, opacity: 1 }}
          transition={{ duration: 0.5 }}
        >
          <h1 className="title">Neon Tic Tac Toe</h1>
          <div className="status-card">
            <div className={`player-info ${isXNext ? 'active x' : ''}`}>
              <X size={20} className="icon-x" />
              <span>Player X</span>
            </div>
            <div className={`player-info ${!isXNext ? 'active o' : ''}`}>
              <Circle size={18} className="icon-o" />
              <span>Player O</span>
            </div>
          </div>
        </motion.header>

        <div className="board-wrapper">
          <motion.div
            className="board"
            initial={{ scale: 0.8, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            transition={{ duration: 0.5, delay: 0.2 }}
          >
            {board.map((square, i) => (
              <motion.button
                key={i}
                className={`square ${winningLine?.includes(i) ? 'winner' : ''} ${square ? 'occupied' : ''}`}
                onClick={() => handleClick(i)}
                whileHover={!square && !winner ? { backgroundColor: 'rgba(255, 255, 255, 0.05)' } : {}}
                whileTap={!square && !winner ? { scale: 0.95 } : {}}
              >
                <AnimatePresence mode="wait">
                  {square === 'X' && (
                    <motion.div
                      key="X"
                      initial={{ scale: 0, rotate: -45 }}
                      animate={{ scale: 1, rotate: 0 }}
                      exit={{ scale: 0 }}
                      className="symbol x-symbol"
                    >
                      <X size={48} strokeWidth={3} />
                    </motion.div>
                  )}
                  {square === 'O' && (
                    <motion.div
                      key="O"
                      initial={{ scale: 0 }}
                      animate={{ scale: 1 }}
                      exit={{ scale: 0 }}
                      className="symbol o-symbol"
                    >
                      <Circle size={42} strokeWidth={3} />
                    </motion.div>
                  )}
                </AnimatePresence>
              </motion.button>
            ))}
          </motion.div>
        </div>

        <AnimatePresence>
          {winner && (
            <motion.div
              className="result-overlay"
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: 20 }}
            >
              <div className="result-content">
                {winner === 'Draw' ? (
                  <>
                    <User size={48} className="draw-icon" />
                    <h2>It's a Draw!</h2>
                  </>
                ) : (
                  <>
                    <Trophy size={48} className={`trophy-icon ${winner.toLowerCase()}`} />
                    <h2>Player {winner} Wins!</h2>
                  </>
                )}
                <button className="reset-button" onClick={resetGame}>
                  <RefreshCcw size={20} />
                  Play Again
                </button>
              </div>
            </motion.div>
          )}
        </AnimatePresence>

        {!winner && (
          <motion.button
            className="reset-fab"
            onClick={resetGame}
            whileHover={{ rotate: 180 }}
            transition={{ duration: 0.5 }}
          >
            <RefreshCcw size={24} />
          </motion.button>
        )}
      </div>
    </div>
  );
};

export default App;
