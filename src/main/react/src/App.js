// @flow
import React, {Component} from 'react';
import './App.css';
import {adalLogOut, adalFetch} from './adal';

class App extends Component<{}> {

    constructor() {
        super();
        this.state = {
            todos: [],
            loading: false,
            sending: false,
            newTodoName: ''
        }
    }

    componentWillMount() {
        this.getTodos();
    }

    logOut() {
        console.log('logging out...');
        adalLogOut();
    }

    getTodos = (showLoading = true) => {
        console.log('Sending get request...');
        showLoading && this.setState({loading: true});
        adalFetch('/api/todolist')
            .then(response => response.json())
            .then(todos => {
                this.setState({
                    loading: false,
                    todos,
                });
            });
    };

    addTodo = () => {
        const descr = this.state.newTodoName;
        if (descr) {
            this.setState({sending: true});
            console.log('Sending post request...');
            adalFetch('/api/todolist', {
                method: "POST",
                credentials: "include",
                body: JSON.stringify({
                    Description: descr,
                    Owner: 'TestUserNEW'
                })
            }).then(response => response.text())
                .then(resp => {
                    console.log('Post result: ', resp);
                    this.setState({
                        sending: false,
                        newTodoName: ''
                    });
                    this.getTodos(false);
                });
        }
    };

    render() {
        const todoItems = (this.state.todos.length > 0) && this.state.todos.map(todo => {
            return (
                <div key={todo.ID} className="line">
                    <span>{todo.ID}</span>
                    <span>{todo.Description}</span>
                    <span>{todo.Owner}</span>
                </div>
            )
        });
        const todoList = (this.state.todos.length > 0) &&
            <div className="todos">
                <div className="line">
                    <span>ID</span>
                    <span>Description</span>
                    <span>Owner</span>
                </div>
                {todoItems}
            </div>;

        return (
            <div className="App">
                <header className="App-header">
                    <h1 className="App-title">Azure AD authentication test</h1>
                    <button onClick={this.logOut}>Logout</button>
                </header>
                <p className="App-intro">
                    Edit <code>adal/adal-config.js</code> and change <code>tenant</code>, <code>clientId</code> and <code>endpoints</code>.
                </p>
                <div className="main">
                    <div>
                        <input type="text" value={this.state.newTodoName} onChange={(e)=> {this.setState({newTodoName: e.target.value})}} />
                        <button onClick={this.addTodo} disabled={this.state.sending}>Add</button>
                    </div>
                    {this.state.loading && <h3>Loading...</h3>}
                    {todoList}
                </div>
            </div>
        );
    }
}

export default App;
