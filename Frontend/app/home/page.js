"use client";

import React, { useState } from 'react';

export default function HomePage() {
  const [processes, setProcesses] = useState([]);
  const [formData, setFormData] = useState({
    processId: '',
    arrivalTime: '',
    burstTime: '',
  });
  const [selectedAlgo, setSelectedAlgo] = useState('');
  const [timeQuantum, setTimeQuantum] = useState('');
  const [results, setResults] = useState(null);
  const [activeTab, setActiveTab] = useState('input'); // 'input' or 'results'

  const schedulingAlgorithms = [
    { value: 'FCFS', name: 'First-Come, First-Served (FCFS)' },
    { value: 'RR', name: 'Round Robin (RR)' },
    { value: 'NP-SJF', name: 'Non-Primitive Shortest Job First(NP-SJF)' },
    { value: 'P-SJF', name: 'Primitive Shortest Job First(P-SJF)' },
    { value: 'NP-PS', name: 'Priority (Non-Primitive)' },
    { value: 'P-PS', name: 'Priority (Primitive)' }
  ];

  const handleAddProcess = () => {
    if (formData.processId && formData.arrivalTime !== '' && formData.burstTime !== '') {
      setProcesses([...processes, {
        processId: formData.processId,
        arrivalTime: parseInt(formData.arrivalTime),
        burstTime: parseInt(formData.burstTime)
      }]);
      setFormData({ processId: '', arrivalTime: '', burstTime: '' });
    }
  };

  const handleSubmit = async () => {
    if (!selectedAlgo || processes.length === 0) {
      alert('Please select an algorithm and add at least one process.');
      return;
    }

    try {
      const requestBody = {
        processes: processes,
        timeQuantum: selectedAlgo === 'RR' ? parseInt(timeQuantum) : undefined
      };

      const response = await fetch(`http://localhost:8080/api/scheduler/${selectedAlgo.toLowerCase()}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(requestBody)
      });

      const result = await response.json();
      setResults(result);
      setActiveTab('results');
    } catch (error) {
      console.error('Error submitting data:', error);
    }
  };

  const handleRemoveProcess = (index) => {
    const updatedProcesses = [...processes];
    updatedProcesses.splice(index, 1);
    setProcesses(updatedProcesses);
  };

  const handleReset = () => {
    setProcesses([]);
    setSelectedAlgo('');
    setResults(null);
    setActiveTab('input');
  };

  const calculateAverages = (results) => {
    if (!results || results.length === 0) return null;
    
    const avgTAT = results.reduce((sum, proc) => sum + proc.turnAroundTime, 0) / results.length;
    const avgWT = results.reduce((sum, proc) => sum + proc.waitingTime, 0) / results.length;
    
    return {
      avgTAT: avgTAT.toFixed(2),
      avgWT: avgWT.toFixed(2)
    };
  };

  
  const generateGanttData = (results, selectedAlgo) => {
    if (selectedAlgo === 'P-SJF' || selectedAlgo === 'RR') {
      if (!results || results.length === 0 || !results[0].sequence) return [];
      
      return results[0].sequence.map(item => ({
        processId: item.processId,
        start: item.startTime,
        end: item.endTime,
        duration: item.endTime - item.startTime
      }));
    } else {
      if (!results || results.length === 0) return [];
      
      const sortedResults = [...results].sort((a, b) => a.criticalTime - b.criticalTime);
      
      let ganttData = [];
      let prevTime = 0;
      
      sortedResults.forEach(proc => {
        if (proc.arivalTime > prevTime) {
          ganttData.push({
            processId: 'Idle',
            start: prevTime,
            end: proc.arivalTime
          });
          prevTime = proc.arivalTime;
        }
        
        ganttData.push({
          processId: proc.name,
          start: prevTime,
          end: proc.criticalTime
        });
        
        prevTime = proc.criticalTime;
      });
      
      return ganttData;
    }
  };
  
  const averages = results ? calculateAverages(results) : null;
  const ganttData = results ? generateGanttData(results, selectedAlgo) : [];

  return (
    <div className="min-h-screen bg-gradient-to-br from-indigo-900 to-purple-800 text-white">
      <div className="container mx-auto px-4 py-8">
        <header className="mb-8 text-center">
          <h1 className="text-4xl font-bold mb-2 bg-clip-text text-transparent bg-gradient-to-r from-amber-300 to-amber-500">
            CPU Scheduling Simulator
          </h1>
          <p className="text-lg text-purple-200">
            Visualize and compare different CPU scheduling algorithms
          </p>
        </header>

        <div className="flex flex-col lg:flex-row gap-6">
          {/* Left Panel - Input Section */}
          <div className="lg:w-1/3 bg-white/10 backdrop-blur-md rounded-xl shadow-2xl p-6 border border-white/20">
            <h2 className="text-2xl font-semibold mb-6 text-amber-300 flex items-center">
              <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
              </svg>
              Add Process
            </h2>

            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-purple-100 mb-1">Process ID</label>
                <input
                  type="text"
                  value={formData.processId}
                  onChange={(e) => setFormData({ ...formData, processId: e.target.value })}
                  className="w-full px-4 py-2 rounded-lg bg-white/10 border border-white/20 focus:outline-none focus:ring-2 focus:ring-amber-400 transition"
                  placeholder="P1"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-purple-100 mb-1">Arrival Time</label>
                <input
                  type="number"
                  value={formData.arrivalTime}
                  onChange={(e) => setFormData({ ...formData, arrivalTime: e.target.value })}
                  className="w-full px-4 py-2 rounded-lg bg-white/10 border border-white/20 focus:outline-none focus:ring-2 focus:ring-amber-400 transition"
                  placeholder="0"
                  min="0"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-purple-100 mb-1">Burst Time</label>
                <input
                  type="number"
                  value={formData.burstTime}
                  onChange={(e) => setFormData({ ...formData, burstTime: e.target.value })}
                  className="w-full px-4 py-2 rounded-lg bg-white/10 border border-white/20 focus:outline-none focus:ring-2 focus:ring-amber-400 transition"
                  placeholder="5"
                  min="1"
                />
              </div>

              <button
                onClick={handleAddProcess}
                className="w-full py-3 bg-gradient-to-r from-amber-500 to-amber-600 rounded-lg font-medium hover:from-amber-600 hover:to-amber-700 transition-all shadow-lg hover:shadow-amber-500/30 flex items-center justify-center"
              >
                <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
                  <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-11a1 1 0 10-2 0v2H7a1 1 0 100 2h2v2a1 1 0 102 0v-2h2a1 1 0 100-2h-2V7z" clipRule="evenodd" />
                </svg>
                Add Process
              </button>
            </div>

            <div className="mt-8">
              <h3 className="text-xl font-semibold mb-4 text-amber-300 flex items-center">
                <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2z" />
                </svg>
                Scheduling Algorithm
              </h3>
              
              <select
                value={selectedAlgo}
                onChange={(e) => setSelectedAlgo(e.target.value)}
                className="w-full px-4 py-3 rounded-lg bg-white/10 border border-white/20 focus:outline-none focus:ring-2 focus:ring-amber-400 transition appearance-none"
              >
                <option value="" disabled className="bg-purple-900">Select Algorithm</option>
                {schedulingAlgorithms.map((algo, index) => (
                  <option key={index} value={algo.value} className="bg-purple-900">{algo.name}</option>
                ))}
              </select>
            </div>

            {selectedAlgo === 'RR' ? (
              <div>
              <label className="block text-sm mt-3 font-medium text-purple-100 mb-1">Time Quantum</label>
              <input
                type="number"
                value={timeQuantum}
                onChange={(e) => setTimeQuantum(e.target.value )}
                className="w-full px-4 py-2 rounded-lg bg-white/10 border border-white/20 focus:outline-none focus:ring-2 focus:ring-amber-400 transition"
                placeholder="Time Quantum"
                min="1"
              />
            </div>
            ) : (
              <div 
                   className='pointer-events-none opacity-50 cursor-not-allowed'
                >
              <label className="block text-sm mt-3 font-medium text-purple-100 mb-1">Time Quantum</label>
              <input
                type="number"
                value={timeQuantum}
                onChange={(e) => setTimeQuantum(e.target.value )}
                className="w-full px-4 py-2 rounded-lg bg-white/10 border border-white/20 focus:outline-none focus:ring-2 focus:ring-amber-400 transition"
                placeholder="Time Quantum"
                min="1"
              />
            </div>
            )}

            <div className="mt-8 space-y-3">
              <button
                onClick={handleSubmit}
                disabled={!selectedAlgo || processes.length === 0}
                className={`w-full py-3 rounded-lg font-medium transition-all shadow-lg flex items-center justify-center
                  ${!selectedAlgo || processes.length === 0 
                    ? 'bg-gray-600 cursor-not-allowed' 
                    : 'bg-gradient-to-r from-green-500 to-teal-500 hover:from-green-600 hover:to-teal-600 hover:shadow-green-500/30'}`}
              >
                <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 10V3L4 14h7v7l9-11h-7z" />
                </svg>
                Run Simulation
              </button>
              
              <button
                onClick={handleReset}
                className="w-full py-3 bg-gradient-to-r from-red-500 to-pink-600 rounded-lg font-medium hover:from-red-600 hover:to-pink-700 transition-all shadow-lg hover:shadow-red-500/30 flex items-center justify-center"
              >
                <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                </svg>
                Reset All
              </button>
            </div>
          </div>

          {/* Right Panel - Results Section */}
          <div className="lg:w-2/3 bg-white/10 backdrop-blur-md rounded-xl shadow-2xl p-6 border border-white/20">
            <div className="flex border-b border-white/20 mb-6">
              <button
                onClick={() => setActiveTab('input')}
                className={`px-4 py-2 font-medium transition-all ${activeTab === 'input' ? 'text-amber-300 border-b-2 border-amber-300' : 'text-purple-200 hover:text-white'}`}
              >
                Process List
              </button>
              <button
                onClick={() => setActiveTab('results')}
                disabled={!results}
                className={`px-4 py-2 font-medium transition-all ${!results ? 'text-gray-500 cursor-not-allowed' : activeTab === 'results' ? 'text-amber-300 border-b-2 border-amber-300' : 'text-purple-200 hover:text-white'}`}
              >
                Simulation Results
              </button>
            </div>

            {activeTab === 'input' ? (
              <div>
                <h3 className="text-xl font-semibold mb-4 text-amber-300">Current Process Queue</h3>
                
                {processes.length > 0 ? (
                  <div className="overflow-x-auto">
                    <table className="w-full border-collapse">
                      <thead>
                        <tr className="bg-white/10">
                          <th className="px-4 py-3 text-left rounded-tl-lg">Process ID</th>
                          <th className="px-4 py-3 text-left">Arrival Time</th>
                          <th className="px-4 py-3 text-left">Burst Time</th>
                          <th className="px-4 py-3 text-right rounded-tr-lg">Actions</th>
                        </tr>
                      </thead>
                      <tbody className="divide-y divide-white/10">
                        {processes.map((proc, index) => (
                          <tr key={index} className="hover:bg-white/5 transition">
                            <td className="px-4 py-3 font-mono">{proc.processId}</td>
                            <td className="px-4 py-3">{proc.arrivalTime}</td>
                            <td className="px-4 py-3">{proc.burstTime}</td>
                            <td className="px-4 py-3 text-right">
                              <button 
                                onClick={() => handleRemoveProcess(index)}
                                className="px-3 py-1 bg-red-500/90 hover:bg-red-600 rounded text-sm transition"
                              >
                                Remove
                              </button>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                ) : (
                  <div className="text-center py-8 bg-white/5 rounded-lg">
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-12 w-12 mx-auto text-purple-300 mb-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                    </svg>
                    <p className="text-purple-200">No processes added yet. Add processes to begin simulation.</p>
                  </div>
                )}
              </div>
            ) : (
              <div>
                {results ? (
                  <div className="space-y-8">
                    <div className="bg-gradient-to-r from-purple-900/50 to-indigo-900/50 p-6 rounded-xl border border-white/10">
                      <h3 className="text-2xl font-bold mb-4 text-center text-amber-300">
                        {selectedAlgo} Algorithm Results
                      </h3>
                      
                      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
                        <div className="bg-white/5 p-4 rounded-lg border border-white/10">
                          <h4 className="text-sm font-semibold text-purple-200 mb-2">AVERAGE TURNAROUND TIME</h4>
                          <p className="text-3xl font-bold text-green-400">{averages.avgTAT}</p>
                        </div>
                        <div className="bg-white/5 p-4 rounded-lg border border-white/10">
                          <h4 className="text-sm font-semibold text-purple-200 mb-2">AVERAGE WAITING TIME</h4>
                          <p className="text-3xl font-bold text-blue-400">{averages.avgWT}</p>
                        </div>
                      </div>
                    </div>

                    <div>
                      <h4 className="text-lg font-semibold mb-3 text-amber-300">Process Metrics</h4>
                      <div className="overflow-x-auto">
                        <table className="w-full border-collapse">
                          <thead>
                            <tr className="bg-white/10">
                              <th className="px-4 py-3 text-left rounded-tl-lg">Process</th>
                              <th className="px-4 py-3 text-left">Arrival</th>
                              <th className="px-4 py-3 text-left">Burst</th>
                              <th className="px-4 py-3 text-left">Completion</th>
                              <th className="px-4 py-3 text-left">Turnaround</th>
                              <th className="px-4 py-3 text-left rounded-tr-lg">Waiting</th>
                            </tr>
                          </thead>
                          <tbody className="divide-y divide-white/10">
                            {results.map((proc, index) => (
                              <tr key={index} className="hover:bg-white/5 transition">
                                <td className="px-4 py-3 font-mono">{proc.name}</td>
                                <td className="px-4 py-3">{proc.arivalTime}</td>
                                <td className="px-4 py-3">{proc.burstTime}</td>
                                <td className="px-4 py-3">{proc.criticalTime}</td>
                                <td className="px-4 py-3">{proc.turnAroundTime}</td>
                                <td className="px-4 py-3">{proc.waitingTime}</td>
                              </tr>
                            ))}
                          </tbody>
                        </table>
                      </div>
                    </div>

                    <div>
                      <h4 className="text-lg font-semibold mb-3 text-amber-300">Gantt Chart</h4>
                      <div className="bg-white/5 p-4 rounded-xl border border-white/10 overflow-x-auto">
                        <div className="flex items-center h-20 min-w-max">
                          {ganttData.map((item, index) => (
                            <div key={index} className="flex flex-col items-center mx-1">
                              <div 
                                className={`h-12 flex items-center justify-center border border-white/20 font-mono text-sm
                                  ${item.processId === 'Idle' 
                                    ? 'bg-gray-600/80' 
                                    : 'bg-gradient-to-b from-blue-500 to-blue-600'} rounded-t-lg`}
                                style={{ width: `${Math.max(60, (item.end - item.start) * 30)}px` }}
                              >
                                {item.processId}
                              </div>
                              <div className="text-xs mt-1 bg-white/10 px-2 py-1 rounded-b-lg">
                                {item.start}-{item.end}
                              </div>
                            </div>
                          ))}
                        </div>
                      </div>
                      <div className="mt-3 flex justify-center space-x-4">
                        <div className="flex items-center">
                          <div className="w-4 h-4 rounded bg-blue-500 mr-2"></div>
                          <span className="text-sm">Process Execution</span>
                        </div>
                        <div className="flex items-center">
                          <div className="w-4 h-4 rounded bg-gray-600 mr-2"></div>
                          <span className="text-sm">Idle Time</span>
                        </div>
                      </div>
                    </div>
                  </div>
                ) : (
                  <div className="text-center py-12">
                    <div className="inline-flex items-center justify-center bg-white/10 p-4 rounded-full mb-4">
                      <svg xmlns="http://www.w3.org/2000/svg" className="h-10 w-10 text-amber-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
                      </svg>
                    </div>
                    <h4 className="text-xl font-semibold mb-2">No Results Yet</h4>
                    <p className="text-purple-200 max-w-md mx-auto">
                      Run a simulation with the selected algorithm to see detailed results and visualization.
                    </p>
                  </div>
                )}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}