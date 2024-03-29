import React, {useEffect, useRef, useState} from 'react'

import {useHistory, useLocation} from 'react-router-dom';

import classNames from 'classnames/bind';

import styles from './guavaHeader.module.scss';
import {useRecoilState} from 'recoil';
import {centerState, levelState, regionState} from '../datatool/state';
import ArrowLeftOutlined from '@ant-design/icons/es/icons/ArrowLeftOutlined';
import {Icon, List, Result, WingBlank} from 'antd-mobile';
import {fetchSearch} from '../datatool/api';
import CloseOutlined from '@ant-design/icons/es/icons/CloseOutlined';
import ReconciliationOutlined from '@ant-design/icons/es/icons/ReconciliationOutlined';
import EnvironmentOutlined from '@ant-design/icons/es/icons/EnvironmentOutlined';
import Highlighter from 'react-highlight-words';
import {useLocalStorage} from '../common/useLocalStorage';
import queryString from 'query-string';
import SearchOutlined from '@ant-design/icons/es/icons/SearchOutlined';

const cx = classNames.bind(styles);

const GuavaSearchHeader = () => {
    const location = useLocation();
    const history = useHistory();
    // const queryInput = useRef(null);
    const [query, setQuery] = useState('');
    const [queryList, setQueryList] = useState([]);
    const [loading, setLoading] = useState(false);
    const [level, setLevel] = useRecoilState(levelState);
    const [region, setRegion] = useRecoilState(regionState);
    const [searchList, setSearchList] = useLocalStorage('searchList', []);
    const [center, setCenter] = useLocalStorage('storageCenter', {lat: 37.3614463, lng: 127.1114893});
    // const [center, setCenter] = useRecoilState(centerState);

    useEffect(() => {
        console.log('search header');
    }, []);
    //
    // useEffect(() => {
    //     if (queryInput && queryInput.current) {
    //         queryInput.current.focus();
    //     }
    // }, [queryInput]);

    useEffect(() => {
        fetchRegion();
    }, [query]);

    const onChangeQuery = (e) => {
        let query = e.target.value;
        setQuery(query);
        if (query.length === 0) {
            setQueryList([]);
        }
    };

    const fetchRegion = async () => {
        if (query !== '') {
            const pattern = /([^가-힣a-z\x20])/i;
            if (!pattern.test(query)) {
                setLoading(true);
                let regionList = await fetchSearch(query);
                console.log(regionList);
                setQueryList(regionList);
                setLoading(false);
            }
        }
    };

    const handleResultItem = async (item) => {
        setRegion(item);
        let temp = searchList.filter(x => !(x.type === 'BUILDING' ? x.buildingId === item.buildingId : x.id === item.id));
        temp.unshift(item);
        if (temp.length > 10) {
            temp = temp.subList(0, 10);
        }
        setSearchList(temp);
        // history.replace('/');
        setCenter({lat: item.lat, lng: item.lng});

        if (item.type === 'BUILDING') {
            history.push('/b/' + item.buildingId);
        } else {
            history.push('/r/' + item.id);
        }

        // if (location.pathname === '/search') {
        //     history.replace('/');
        //     setCenter({lat: item.lat, lng: item.lng});
        // } else {
        //     if (item.type === 'BUILDING') {
        //         history.push('/b/' + item.buildingId);
        //     } else {
        //         history.push('/r/' + item.id);
        //     }
        // }
    };

    const removeSearchList = (item) => {
        let temp = searchList.filter(x => !(x.type === 'BUILDING' ? x.buildingId === item.buildingId : x.id === item.id));
        setSearchList(temp);
    };

    return (
        <>
            <div className={cx('w-full fixed z-99999')}>
                <div className={cx('flex justify-between h-12 p-3 bg-guava')}>
                    <div onClick={() => history.push('/')}
                         className={cx('flex items-center flex-none text-center text-white transform hover:scale-110 motion-reduce:transform-none cursor-pointer')}>
                        <svg className="w-5 h-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"
                             stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                  d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
                        </svg>
                    </div>
                    <div onClick={() => history.push('/search')}
                         className={cx('flex items-center justify-start flex-grow ml-3 text-white text-base font-light')}>
                        <input
                            className={cx('w-full flex items-center justify-start bg-green-700 flex-grow text-white text-base font-light rounded placeholder-gray-300')}
                            autoFocus={true} ref={(ref) => {
                            if (ref !== null) {
                                ref.focus();
                            }
                        }}
                            value={query}
                            onKeyPress={(e) => {
                                if (e.key === 'Enter') {
                                    fetchRegion();
                                }
                            }}
                            onChange={onChangeQuery}
                            placeholder="지역/아파트 검색하기">
                        </input>
                    </div>
                    <div onClick={() => history.push('/')}
                         className={cx('flex items-center flex-none text-center text-white')}>
                        <svg className="w-5 h-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"
                             stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                  d="M6 18L18 6M6 6l12 12"/>
                        </svg>
                    </div>
                </div>
                {
                    queryList.length > 0 &&
                    <div className={cx('bg-white max-h-64 overflow-auto border-b border-gray-300 pb-1')}>
                        {
                            queryList.map(x =>
                                <div className={cx('ml-3 mr-3 pt-2 pb-2 border-b')} onClick={() => handleResultItem(x)}>
                                    <div className={cx('text-xs text-gray-600')}>
                                        <Highlighter
                                            style={{marginLeft: 4}}
                                            highlightClassName={cx('bg-white text-guava font-bold')}
                                            searchWords={query.split('')}
                                            autoEscape={true}
                                            textToHighlight={x.name}
                                        />
                                    </div>
                                    <div className={cx('text-xs text-gray-400')}>
                                        <Highlighter
                                            style={{marginLeft: 4}}
                                            highlightClassName={cx('bg-white text-guava font-bold')}
                                            searchWords={query.split('')}
                                            autoEscape={true}
                                            textToHighlight={x.address}
                                        />
                                    </div>
                                </div>
                            )
                        }
                    </div>
                }
            </div>
            {/*<div className={cx('header_container')}>*/}
            {/*    /!*<div className={cx('title_container')}>*!/*/}
            {/*    /!*    <WingBlank>*!/*/}
            {/*    /!*        <div className={cx('left')}>*!/*/}
            {/*    /!*            <SearchOutlined/>*!/*/}
            {/*    /!*            /!*<ArrowLeftOutlined/>*!/*!/*/}
            {/*    /!*        </div>*!/*/}
            {/*    /!*    </WingBlank>*!/*/}
            {/*    /!*    <div className={cx('center')}>*!/*/}
            {/*    /!*        /!*<span className={cx('title')}>{region.address}</span>*!/*!/*/}
            {/*    /!*        <input autoFocus={true} ref={(ref) => {*!/*/}
            {/*    /!*            if (ref !== null) {*!/*/}
            {/*    /!*                ref.focus();*!/*/}
            {/*    /!*            }*!/*/}
            {/*    /!*        }}*!/*/}
            {/*    /!*               value={query}*!/*/}
            {/*    /!*               onKeyPress={(e) => {*!/*/}
            {/*    /!*                   if (e.key === 'Enter') {*!/*/}
            {/*    /!*                       fetchRegion();*!/*/}
            {/*    /!*                   }*!/*/}
            {/*    /!*               }}*!/*/}
            {/*    /!*               onChange={onChangeQuery}*!/*/}
            {/*    /!*               placeholder="지역/아파트 검색하기">*!/*/}
            {/*    /!*        </input>*!/*/}
            {/*    /!*    </div>*!/*/}
            {/*    /!*    <WingBlank>*!/*/}
            {/*    /!*        <div className={cx('right')}*!/*/}
            {/*    /!*             onClick={() => history.push('/')}>*!/*/}
            {/*    /!*            /!*<SearchOutlined/>*!/*!/*/}
            {/*    /!*            <CloseOutlined/>*!/*/}
            {/*    /!*        </div>*!/*/}
            {/*    /!*        /!*<div className={cx('right')} onClick={() => history.push(location.pathname.replace('/search', ''))}>*!/*!/*/}
            {/*    /!*        /!*<div className={cx('right')}>*!/*!/*/}
            {/*    /!*        /!*    <CloseOutlined style={{visibility: 'hidden'}}/>*!/*!/*/}
            {/*    /!*        /!*</div>*!/*!/*/}
            {/*    /!*    </WingBlank>*!/*/}
            {/*    /!*</div>*!/*/}
            {/*    /!*<div className={cx('query_container')}>*!/*/}
            {/*    /!*    {*!/*/}
            {/*    /!*        (!loading && query.length === 0) &&*!/*/}
            {/*    /!*        <>*!/*/}
            {/*    /!*            {*!/*/}
            {/*    /!*                searchList.length > 0 &&*!/*/}
            {/*    /!*                <List.Item>*!/*/}
            {/*    /!*                    <span className={cx('search_list_title')}>최근검색어</span><span*!/*/}
            {/*    /!*                    style={{marginLeft: 2, fontSize: 9, color: '#8C8C8C'}}>(최대 10개)</span>*!/*/}
            {/*    /!*                </List.Item>*!/*/}
            {/*    /!*            }*!/*/}
            {/*    /!*            {*!/*/}
            {/*    /!*                searchList.map(x =>*!/*/}
            {/*    /!*                    <List.Item>*!/*/}
            {/*    /!*                        <div className={cx('search_list')}>*!/*/}
            {/*    /!*                            <div className={cx('left')} onClick={() => handleResultItem(x)}>*!/*/}
            {/*    /!*                                {*!/*/}
            {/*    /!*                                    x.type === 'BUILDING' ?*!/*/}
            {/*    /!*                                        <ReconciliationOutlined/> :*!/*/}
            {/*    /!*                                        <EnvironmentOutlined/>*!/*/}
            {/*    /!*                                }*!/*/}
            {/*    /!*                                <Highlighter*!/*/}
            {/*    /!*                                    style={{marginLeft: 4, fontSize: 12}}*!/*/}
            {/*    /!*                                    highlightClassName={cx('highlight')}*!/*/}
            {/*    /!*                                    searchWords={query.split('')}*!/*/}
            {/*    /!*                                    autoEscape={true}*!/*/}
            {/*    /!*                                    textToHighlight={x.name}*!/*/}
            {/*    /!*                                />*!/*/}
            {/*    /!*                                <List.Item.Brief style={{*!/*/}
            {/*    /!*                                    fontSize: 10,*!/*/}
            {/*    /!*                                    marginTop: 2,*!/*/}
            {/*    /!*                                    marginBottom: 4*!/*/}
            {/*    /!*                                }}>*!/*/}
            {/*    /!*                                    <Highlighter*!/*/}
            {/*    /!*                                        highlightClassName={cx('highlight')}*!/*/}
            {/*    /!*                                        searchWords={query.split('')}*!/*/}
            {/*    /!*                                        autoEscape={true}*!/*/}
            {/*    /!*                                        textToHighlight={x.address}*!/*/}
            {/*    /!*                                    />*!/*/}
            {/*    /!*                                </List.Item.Brief>*!/*/}
            {/*    /!*                            </div>*!/*/}
            {/*    /!*                            <div className={cx('right')} onClick={() => removeSearchList(x)}>*!/*/}
            {/*    /!*                                <CloseOutlined style={{fontSize: 12}}/>*!/*/}
            {/*    /!*                            </div>*!/*/}
            {/*    /!*                        </div>*!/*/}
            {/*    /!*                    </List.Item>*!/*/}
            {/*    /!*                )*!/*/}
            {/*    /!*            }*!/*/}
            {/*    /!*        </>*!/*/}
            {/*    /!*    }*!/*/}
            {/*    /!*    <List>*!/*/}
            {/*    /!*        {*!/*/}
            {/*    /!*            queryList.map(x => {*!/*/}
            {/*    /!*                    if (x.type === 'BUILDING') {*!/*/}
            {/*    /!*                        return (*!/*/}
            {/*    /!*                            <List.Item*!/*/}
            {/*    /!*                                onClick={() => handleResultItem(x)}>*!/*/}
            {/*    /!*                                <ReconciliationOutlined/>*!/*/}
            {/*    /!*                                <Highlighter*!/*/}
            {/*    /!*                                    style={{marginLeft: 4}}*!/*/}
            {/*    /!*                                    highlightClassName={cx('highlight')}*!/*/}
            {/*    /!*                                    searchWords={query.split('')}*!/*/}
            {/*    /!*                                    autoEscape={true}*!/*/}
            {/*    /!*                                    textToHighlight={x.name}*!/*/}
            {/*    /!*                                /><List.Item.Brief*!/*/}
            {/*    /!*                                style={{*!/*/}
            {/*    /!*                                    fontSize: '0.7rem',*!/*/}
            {/*    /!*                                    marginTop: 2,*!/*/}
            {/*    /!*                                    marginBottom: 4*!/*/}
            {/*    /!*                                }}>*!/*/}
            {/*    /!*                                <Highlighter*!/*/}
            {/*    /!*                                    highlightClassName={cx('highlight')}*!/*/}
            {/*    /!*                                    searchWords={query.split('')}*!/*/}
            {/*    /!*                                    autoEscape={true}*!/*/}
            {/*    /!*                                    textToHighlight={x.address}*!/*/}
            {/*    /!*                                />*!/*/}
            {/*    /!*                            </List.Item.Brief></List.Item>*!/*/}
            {/*    /!*                        )*!/*/}
            {/*    /!*                    } else {*!/*/}
            {/*    /!*                        return (*!/*/}
            {/*    /!*                            <List.Item onClick={() => handleResultItem(x)}>*!/*/}
            {/*    /!*                                <EnvironmentOutlined/>*!/*/}
            {/*    /!*                                <Highlighter*!/*/}
            {/*    /!*                                    style={{marginLeft: 4}}*!/*/}
            {/*    /!*                                    highlightClassName={cx('highlight')}*!/*/}
            {/*    /!*                                    searchWords={query.split('')}*!/*/}
            {/*    /!*                                    autoEscape={true}*!/*/}
            {/*    /!*                                    textToHighlight={x.address}*!/*/}
            {/*    /!*                                />*!/*/}
            {/*    /!*                            </List.Item>*!/*/}
            {/*    /!*                        )*!/*/}
            {/*    /!*                    }*!/*/}
            {/*    /!*                }*!/*/}
            {/*    /!*            )*!/*/}
            {/*    /!*        }*!/*/}
            {/*    /!*    </List>*!/*/}
            {/*    /!*</div>*!/*/}
            {/*    /!*<div className={cx('footer_container')}>*!/*/}
            {/*    /!*    <div>검색결과 {queryList.length}개</div>*!/*/}
            {/*    /!*    <div onClick={() => history.push('/')}>닫기</div>*!/*/}
            {/*    /!*</div>*!/*/}
            {/*</div>*/}
        </>
    )
};

export default GuavaSearchHeader;