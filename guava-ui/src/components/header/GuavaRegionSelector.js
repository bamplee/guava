import React, {useEffect, useState} from 'react'

import {useHistory, useLocation} from 'react-router-dom';
import {Badge, Button, List, Tabs} from 'antd-mobile';

import classNames from 'classnames/bind';

import styles from './guavaRegionSelector.module.scss';
import {useRecoilState} from 'recoil';
import {
    buildingListState,
    isQueryModeState,
    levelState,
    queryListState,
    regionState,
} from '../datatool/state';
import RightOutlined from '@ant-design/icons/es/icons/RightOutlined';
import {getBuildings, getRegionChildList} from '../datatool/api';
import GuavaLoading from '../detail/GuavaLoading';
import {TABLE_OPTION} from '../constant';

const cx = classNames.bind(styles);

const GuavaRegionSelector = () => {
    const location = useLocation();
    const history = useHistory();
    // const [region, setRegion] = useRecoilState(regionState);
    // const queryInput = useRef(null);
    const [regionList, setRegionList] = useState([]);
    const [queryList, setQueryList] = useRecoilState(queryListState);
    const [loading, setLoading] = useState(false);
    const [isQueryMode, setIsQueryMode] = useRecoilState(isQueryModeState);
    const [level, setLevel] = useRecoilState(levelState);
    const [buildingList, setBuildingList] = useRecoilState(buildingListState);
    const [region, setRegion] = useRecoilState(regionState);

    const init = () => {
        if (region.type === 'BUILDING') {
            handleSearcbBuilding(region.id);
        } else if (region.type === 'SIDO') {
            handleSearchItem(region.sidoCode, 'SIDO');
        } else if (region.type === 'SIGUNGU') {
            handleSearchItem(region.sidoCode, 'SIGUNGU');
        } else if (region.type === 'DONG') {
            handleSearchItem(region.sidoCode + region.sigunguCode, 'DONG');
        } else if (region.type === 'RI') {
            handleSearchItem(region.sidoCode + region.sigunguCode + region.dongCode, 'RI');
        }
    };

    useEffect(() => {
        init();
    }, [region]);

    const handleSearchItem = async (code, type) => {
        setIsQueryMode(false);
        let result = await getRegionChildList(code, type);
        setRegionList(result);
        // history.push('/detail/' + region.buildingId);
    };

    const handleSearcbBuilding = async (regionId) => {
        setIsQueryMode(false);
        let result = await getBuildings(regionId);
        setRegionList(result);
        // history.push('/detail/' + buildingId);
    };

    const handleResultItem = async (item) => {
        setRegion(item);
        if (item.type === 'BUILDING') {
            // setBuildingId(item.buildingId);
            // setRegionId(item.buildingId);
            // let result = await getBuilding(item.buildingId);
            // setRegion(result);
            // setQuery('');
            setLevel(3);
            // setLat(item.lat);
            // setLng(item.lng);
            // setInitCenter({lat: item.lat, lng: item.lng});
            // setShowRegionSelector(false);
            if (location.pathname !== '/search') {
                history.push('/b/' + item.buildingId + '/search');
            }
        } else {
            // let result = await getRegion(item.id);
            // setBuildingId(item.id);
            // setRegionId(item.id);
            if (item.type === 'DONG') {
                setLevel(5);
            } else {
                setLevel(8);
            }
            // sigungu === level8
            // setLat(item.lat);
            // setLng(item.lng);
            // setInitCenter({lat: item.lat, lng: item.lng});
            // setShowRegionSelector(false);
            if (location.pathname !== '/search') {
                history.push('/r/' + item.id + '/search');
            }
        }
    };

    const isActive = (place) => {
        if (place.type === 'SIDO') {
            return place.sidoCode === region.sidoCode;
        } else if (place.type === 'SIGUNGU') {
            return place.sigunguCode === region.sigunguCode;
        } else if (place.type === 'DONG') {
            return place.dongCode === region.dongCode;
        } else if (place.type === 'BUILDING') {
            return place.buildingId === region.buildingId;
        }
    };

    return (
        <div style={{paddingTop: 50}}>
            <div className={cx('query_container')}>
                <Tabs tabs={[
                    {title: <div className={cx('active_tab')}>{region.sidoName}</div>, value: 'SIDO'},
                    {title: <div className={cx(region.sigunguName ? 'active_tab' : 'tab')}>{region.sigunguName ? `${region.sigunguName}` : '시/군/구'}</div>, value: 'SIGUNGU'},
                    {title: <div className={cx(region.dongName ? 'active_tab' : 'tab')}>{region.dongName ? region.dongName : '동'}</div>, value: 'DONG'},
                    {title: <div className={cx(region.buildingName ? 'active_tab' : 'tab')}>{region.buildingName ? region.buildingName : '아파트'}</div>, value: 'APT'},
                ]}
                      onTabClick={(tab, index) => {
                          if(tab.value === 'SIDO') {
                              handleSearchItem(region.sidoCode, 'SIDO');
                          }
                          else if(tab.value === 'SIGUNGU') {
                              handleSearchItem(region.sidoCode, 'SIGUNGU');
                          }
                          else if(tab.value === 'DONG') {
                              handleSearchItem(region.sidoCode + region.sigunguCode, 'DONG');
                          }
                          else {
                              handleSearcbBuilding(region.id);
                          }
                          console.log(tab);
                          // setBeforeMonth(tab.value);
                      }}
                      animated={false} useOnPan={false}
                >
                </Tabs>
                {/*<Tabs tabs={[{title: '시도'}, {title: '시군구'}, {title: '동'}, {title: '아파트'}]} initialPage={2} animated={false} useOnPan={false}/>*/}
                {/*<List className={cx('search_container')}>*/}
                {/*    <List.Item className={cx('search')}>*/}
                {/*        <div className={cx('search_scroll')}>*/}
                {/*            <span className={cx('search_item')}*/}
                {/*                  onClick={() => handleSearchItem(region.sidoCode, 'SIDO')}>*/}
                {/*                {region.sidoName}*/}
                {/*            </span>*/}
                {/*            <span className={cx('divider')}>*/}
                {/*                <RightOutlined/>*/}
                {/*            </span>*/}
                {/*            <span className={cx(region.sigunguName ? 'search_item' : 'empty')}*/}
                {/*                  onClick={() => handleSearchItem(region.sidoCode, 'SIGUNGU')}>*/}
                {/*                {region.sigunguName ? `${region.sigunguName}` : '시/군/구'}*/}
                {/*            </span>*/}
                {/*            <span className={cx('divider')}>*/}
                {/*                <RightOutlined/>*/}
                {/*            </span>*/}
                {/*            <span className={cx(region.dongName ? 'search_item' : 'empty')}*/}
                {/*                  onClick={() => handleSearchItem(region.sidoCode + region.sigunguCode, 'DONG')}>*/}
                {/*                {region.dongName ? region.dongName : '동'}*/}
                {/*            </span>*/}
                {/*            <span className={cx('divider')}>*/}
                {/*                <RightOutlined/>*/}
                {/*            </span>*/}
                {/*            <span*/}
                {/*                className={cx('apt', region.buildingName ? 'search_item' : 'empty')}*/}
                {/*                onClick={() => handleSearcbBuilding(region.id)}>*/}
                {/*                            {region.buildingName ? region.buildingName : '아파트'}*/}
                {/*                        </span>*/}
                {/*        </div>*/}
                {/*    </List.Item>*/}
                {/*</List>*/}
                {
                    isQueryMode ?
                        <div className={cx('search_result')}>
                            <div className={cx('query_container')}>
                                <GuavaLoading isLoading={loading}/>
                                <List>
                                    {
                                        (!loading && queryList.length <= 0 && buildingList.length <= 0) &&
                                        <List.Item>결과없음</List.Item>
                                    }
                                    {
                                        queryList.map(x => <List.Item
                                            onClick={() => handleResultItem(x)}>{(x.sidoName && x.sidoName + ' ') + (x.sigunguName && x.sigunguName + ' ') + (x.dongName && x.dongName)}</List.Item>)
                                    }
                                    {
                                        buildingList.map(x => <List.Item
                                            onClick={() => handleResultItem(x)}>{x.name}<List.Item.Brief
                                            style={{
                                                fontSize: '0.7rem',
                                                marginTop: 2
                                            }}>{x.address}</List.Item.Brief></List.Item>)
                                    }
                                </List>
                            </div>
                        </div> :
                        <div className={cx('search_result')}>
                            {
                                regionList.map(x =>
                                    <div className={cx('result_item', isActive(x) && 'selected')}
                                         onClick={() => handleResultItem(x)}>
                                        <span className={cx('text')}>
                                        {x.summaryName}
                                        </span>
                                    </div>
                                )
                            }
                        </div>
                }
            </div>
        </div>
    )
};

export default GuavaRegionSelector;