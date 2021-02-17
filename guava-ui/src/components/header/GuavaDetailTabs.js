import React, {useState} from 'react'

import {useHistory, useParams} from 'react-router-dom';

import classNames from 'classnames/bind';

import styles from './guavaHeader.module.scss';
import {Tabs} from 'antd-mobile';

const cx = classNames.bind(styles);

const GuavaDetailTabs = ({tabId}) => {
    const history = useHistory();
    // const [region, setRegion] = useRecoilState(regionState);
    const {regionType, buildingId, regionId} = useParams();
    const [tab, setTab] = useState(tabId);

    return (
        <>
            <div style={{borderBottom: '1px solid #f2f2f2'}}>
                <Tabs onChange={(e) => {
                    console.log(e);
                    setTab(e.index);
                    history.replace('/' + regionType + '/' + regionId + '/' + e.index)
                }} tabs={[{title: '실거래', index: 0},
                    {title: '호가', index: 1},
                    {title: '비교', index: 2}]}
                      page={tab.index}
                    // tabBarUnderlineStyle={{border: '1px solid #00802E'}}
                      animated={false}
                      useOnPan={false}/>
            </div>
        </>
    )
};

export default GuavaDetailTabs;