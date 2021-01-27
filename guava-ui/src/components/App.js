import React, {Suspense, useEffect} from 'react'
import {RecoilRoot} from 'recoil'
import {BrowserRouter as Router, Route, Switch} from 'react-router-dom';
import './App.less';
import {ActivityIndicator} from 'antd-mobile';
import GuavaAreaFilter from './common/GuavaAreaFilter';
import ReactGA from 'react-ga';
import {DetailHeaderPage, IntroPage, MapPage} from '../pages';
import SearchPage from '../pages/SearchPage';
import GuavaMatch from './map/GuavaMatch';
import GuavaVersusSearch from './versus/GuavaVersusSearch';

function App() {
    useEffect(() => {
        ReactGA.initialize('UA-129316162-1');
    }, []);

    return (
        <RecoilRoot>
            <Router>
                <Switch>
                    <Suspense fallback={<ActivityIndicator
                        toast
                        text="Loading..."
                        animating={false}
                    />}>
                        <GuavaAreaFilter/>
                        <Route exact path={['/', '/search']} component={MapPage}/>
                        <Route exact path={['/search']} component={SearchPage}/>
                        <Route exact path={['/intro']} component={IntroPage}/>
                        <Route exact path={['/match']} component={GuavaMatch}/>
                        <Route path="/:regionType/:regionId/:tabId?" component={DetailHeaderPage}/>
                        {/*<Route exact path="/:regionType/:regionId/:tabId/search" component={GuavaVersusSearch}/>*/}
                    </Suspense>
                </Switch>
            </Router>
        </RecoilRoot>
    )
}

export default App
