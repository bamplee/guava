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
import GuavaBuildingInfo from './detail/GuavaBuildingInfo';
import GuavaTradeOption from './detail/GuavaTradeOption';
import GuavaChart from './detail/GuavaChart';
import GuavaMarketChart from './detail/GuavaMarketChart';
import GuavaTable from './table/GuavaTable';
import GuavaMarketTable from './table/GuavaMarketTable';
import {DetailHeaderPage, DetailMarketPage, DetailTradePage, IntroPage, MapPage} from '../pages';
import SearchPage from '../pages/SearchPage';

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
                        <Route exact path={['/']} component={MapPage}/>
                        <Route exact path={['/intro']} component={IntroPage}/>
                        <Route exact path={['/search']} component={SearchPage}/>
                        <Route path="/:regionType/:regionId/:tabId?" component={DetailHeaderPage}/>
                    </Suspense>
                </Switch>
            </Router>
        </RecoilRoot>
    )
}

export default App
