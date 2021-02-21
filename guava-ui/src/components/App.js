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
import Detail from "./v2/detail/Detail";
import AreaRangeModal from "./v2/detail/option/AreaRangeModal";
import AreaTypeModal from "./v2/detail/option/AreaTypeModal";

function App() {
    useEffect(() => {
        ReactGA.initialize('UA-129316162-1');
    }, []);

    return (
        <RecoilRoot>
            <Router>
                <Switch>
                    <Suspense fallback={<div>
                        SUSPENSE
                    </div>}>
                        {/*<GuavaAreaFilter/>*/}
                        <AreaRangeModal/>
                        <AreaTypeModal/>
                        <Route exact path={['/search']} component={SearchPage}/>
                        <Route exact path={['/', '/search']} component={MapPage}/>
                        <Route exact path={['/intro']} component={IntroPage}/>
                        <Route exact path={['/match']} component={GuavaMatch}/>
                        <Route path="/:regionType/:regionId/:tabId?" component={Detail}/>
                        <Route exact path="/:regionType/:regionId/:tabId/search" component={GuavaVersusSearch}/>
                    </Suspense>
                </Switch>
            </Router>
        </RecoilRoot>
    )
}

export default App
