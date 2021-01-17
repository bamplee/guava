import React, {Suspense, useEffect} from 'react'
import {RecoilRoot} from 'recoil'
import {BrowserRouter as Router, Route, Switch} from 'react-router-dom';
import './App.less';
import {ActivityIndicator} from 'antd-mobile';
import GuavaAreaFilter from './common/GuavaAreaFilter';
import GuavaAreaTypeFilter from './common/GuavaAreaTypeFilter';
import GuavaDetailPage from './detail/GuavaDetailPage';
import GuavaSearchHeader from './header/GuavaSearchHeader';
import GuavaMap from './map/GuavaMap';
import GuavaMainHeader from './header/GuavaMainHeader';
import GuavaDetailHeader from './header/GuavaDetailHeader';
import GuavaIntroHeader from './header/GuavaIntroHeader';
import ReactGA from 'react-ga';

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
                        <GuavaAreaTypeFilter/>
                        <Route path={['/']} exact>
                            <GuavaMainHeader/>
                            <GuavaMap/>
                        </Route>
                        <Route path={['/intro']} exact>
                            <GuavaIntroHeader/>
                        </Route>
                        <Route path={['/search', '/r/:regionId/search', '/b/:buildingId/search']} exact>
                            <GuavaSearchHeader/>
                        </Route>
                        <Route path={['/r/:regionId', '/b/:buildingId']} exact>
                            <GuavaDetailHeader/>
                            <GuavaDetailPage/>
                        </Route>
                    </Suspense>
                </Switch>
            </Router>
        </RecoilRoot>
    )
}

export default App
