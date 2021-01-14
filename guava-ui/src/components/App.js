import React, {Suspense} from 'react'
import {RecoilRoot} from 'recoil'
import {BrowserRouter as Router, Route, Switch} from 'react-router-dom';
import './App.less';
import {ActivityIndicator} from 'antd-mobile';
import GuavaAreaFilter from './common/GuavaAreaFilter';
import GuavaAreaTypeFilter from './common/GuavaAreaTypeFilter';
import GuavaRegionSelector from './header/GuavaRegionSelector';
import GuavaDetailPage from './detail/GuavaDetailPage';
import GuavaIntro from './common/GuavaIntro';
import GuavaSearchHeader from './header/GuavaSearchHeader';
import GuavaMap from './map/GuavaMap';
import GuavaSearch from './header/GuavaSearch';

function App() {
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
                            <GuavaSearch/>
                            <GuavaMap/>
                        </Route>
                        <Route path={['/intro']} exact>
                            <GuavaIntro/>
                        </Route>
                        <Route path={['/search', '/r/:regionId/search', '/b/:buildingId/search']} exact>
                            {/*<GuavaInit/>*/}
                            <GuavaSearchHeader/>
                            <GuavaRegionSelector/>
                        </Route>
                        {/*<Route path={['/r/:regionId']} exact>*/}
                        {/*    <GuavaInit/>*/}
                        {/*    <GuavaHeader/>*/}
                        {/*    <WhiteSpace/>*/}
                        {/*    <GuavaTradeOption/>*/}
                        {/*    <GuavaChart/>*/}
                        {/*    <GuavaTable/>*/}
                        {/*</Route>*/}
                        <Route path={['/r/:regionId', '/b/:buildingId']} exact>
                            <GuavaDetailPage/>
                        </Route>
                        {/*<Route path={['/r/:regionId/search', '/b/:buildingId/search']} exact>*/}
                        {/*    <GuavaHeader/>*/}
                        {/*    <GuavaRegionSelector/>*/}
                        {/*</Route>*/}
                        {/*<Route path={['/detail/:buildingId/market']} exact>*/}
                        {/*    <GuavaHeader/>*/}
                        {/*    <GuavaRegionInfo/>*/}
                        {/*    <GuavaSpaceLine/>*/}
                        {/*    <GuavaTradeOption/>*/}
                        {/*    <GuavaTradeChart/>*/}
                        {/*    <GuavaMarketList/>*/}
                        {/*</Route>*/}
                    </Suspense>
                </Switch>
            </Router>
        </RecoilRoot>
    )
}

export default App
